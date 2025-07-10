package com.lmar.checkersgame.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.repository.IRoomRepository
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.logic.canContinueJumping
import com.lmar.checkersgame.domain.logic.hasPieces
import com.lmar.checkersgame.domain.logic.isValidMove
import com.lmar.checkersgame.domain.logic.shouldBeKing
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.domain.model.getFirstName
import com.lmar.checkersgame.domain.model.isNotEmpty
import com.lmar.checkersgame.domain.sound.ISoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository,
    private val roomRepository: IRoomRepository,
    private val repository: IGameRepository,
    private val soundPlayer: ISoundPlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _gameState = MutableLiveData<Game>()
    val gameState: LiveData<Game> = _gameState

    private val _roomState = MutableLiveData<Room>()
    val roomState: LiveData<Room> = _roomState

    private val _gameTime = MutableStateFlow(0) // en segundos
    val gameTime: StateFlow<Int> = _gameTime

    private val _rematchRequested = MutableStateFlow(false)
    val rematchRequested: StateFlow<Boolean> = _rematchRequested

    private val _selectedCell = MutableStateFlow<Position?>(null)
    val selectedCell: StateFlow<Position?> = _selectedCell

    var userId: String = ""
    var roomId: String = ""
    var gameId: String = ""

    private var timerJob: Job? = null

    fun startGameTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _gameTime.value += 1
            }
        }
    }

    fun stopGameTimer() {
        timerJob?.cancel()
    }

    init {
        savedStateHandle.get<String>("roomId")?.let {
            roomId = it

            viewModelScope.launch {
                userId = authRepository.getCurrentUser()?.uid.toString()

                val user = userRepository.getUserById(userId)

                if (user != null) {
                    roomRepository.getRoomById(roomId) {
                        _roomState.value = it
                    }

                    val player = Player(userId, user.getFirstName())

                    gameId = repository.createOrJoinGame(player, roomId)

                    repository.listenToGame(gameId) { game ->
                        _gameState.value = game
                        if (game.status == GameStatusEnum.PLAYING) {
                            startGameTimer()
                        } else if (game.status == GameStatusEnum.FINISHED || game.status == GameStatusEnum.ABORTED) {
                            stopGameTimer()
                        }
                    }
                }
            }
        }
    }

    fun onCellClick(row: Int, col: Int) {
        val currentGame = _gameState.value ?: return

        val current = _selectedCell.value
        val pos = row to col
        val piece = currentGame.board[row][col]

        if (piece.isNotEmpty() && piece.playerId == userId && currentGame.turn == userId) {
            _selectedCell.value = pos
        } else if (current != null && current != pos) {
            moveIfValid(current, pos)
        } else {
            _selectedCell.value = null
        }
    }

    fun moveIfValid(from: Position, to: Position) {
        val currentGame = _gameState.value ?: return
        val (player1Id, player2Id) = requirePlayerIds()

        val piece = currentGame.board[from.first][from.second]
        if (piece.playerId != userId) return

        if (!isValidMove(currentGame.board, from, to, userId, player1Id, player2Id)) return

        val newBoard =
            currentGame.board.map { it.map { p -> p.copy() }.toMutableList() }.toMutableList()

        Log.d("Checkers", "from=$from to=$to user=$userId isKing=${piece.isKing}")
        Log.d("Checkers", "shouldBeKing=${shouldBeKing(piece, to.first, player1Id, player2Id)}")

        val isCrowning = shouldBeKing(piece, to.first, player1Id, player2Id)
        val rowDiff = to.first - from.first
        val colDiff = to.second - from.second
        val jumped = kotlin.math.abs(rowDiff) == 2 && kotlin.math.abs(colDiff) == 2

        // Reproducir sonido según el tipo de acción
        when {
            isCrowning && !piece.isKing -> soundPlayer.playCrown()
            jumped -> soundPlayer.playCapture()
            else -> soundPlayer.playMove()
        }

        // Coronación
        newBoard[to.first][to.second] = piece.copy(
            isKing = piece.isKing || isCrowning
        )
        newBoard[from.first][from.second] = Piece() // Limpiar celda

        // Capturar pieza
        if (jumped) {
            val midRow = (from.first + to.first) / 2
            val midCol = (from.second + to.second) / 2
            newBoard[midRow][midCol] = Piece()
        }

        // Guardar el nuevo estado del juego
        _gameState.value = currentGame.copy(board = newBoard)

        if (jumped && canContinueJumping(newBoard, to, userId, player1Id, player2Id)) {
            _selectedCell.value = to
        } else {
            _selectedCell.value = null
            val opponentId = if (userId == player1Id) player2Id else player1Id

            if (!hasPieces(newBoard, opponentId)) {
                endGame(userId)
            } else {
                viewModelScope.launch {
                    repository.updateBoard(gameId, newBoard)
                    repository.updateTurn(gameId, opponentId)
                }
            }
        }
    }

    private fun endGame(winnerId: String) {
        val currentGame = _gameState.value ?: return

        if (winnerId == userId) {
            soundPlayer.playWin()
        } else {
            soundPlayer.playLose()
        }

        viewModelScope.launch {
            repository.setWinner(gameId, winnerId)
            repository.setGameStatus(gameId, GameStatusEnum.FINISHED)

            val player1Id = currentGame.player1?.id.toString()
            val player2Id = currentGame.player2?.id.toString()

            repository.listenToRematchRequests(gameId, player1Id, player2Id) {
                resetGame()
            }
        }
    }

    fun resetGame() {
        val currentGame = _gameState.value ?: return

        _selectedCell.value = null

        val player1 = currentGame.player1
        val player2 = currentGame.player2

        if (player1 == null || player2 == null) return

        viewModelScope.launch {
            val newGameId = repository.createGame(player1, player2, roomId)
            gameId = newGameId
            repository.clearRematchRequests(newGameId)
            repository.listenToGame(newGameId) {
                _gameState.value = it
            }
        }
    }

    fun abortGame() {
        val currentGame = _gameState.value ?: return
        val opponentId = if (userId == currentGame.player1?.id) {
            currentGame.player2?.id
        } else {
            currentGame.player1?.id
        } ?: return

        viewModelScope.launch {
            repository.setWinner(gameId, opponentId)
            repository.setGameStatus(gameId, GameStatusEnum.ABORTED)
            roomRepository.setRoomStatus(roomId, RoomStatusEnum.CLOSED)
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            roomRepository.setRoomStatus(roomId, RoomStatusEnum.CLOSED)
        }
    }

    fun requestRematch() {
        println("Revancha: $userId")
        _rematchRequested.value = true

        viewModelScope.launch {
            repository.requestRematch(gameId, userId)
        }
    }

    private fun requirePlayerIds(): Pair<String, String> {
        val currentGame = _gameState.value ?: error("Game not initialized")
        val player1Id = currentGame.player1?.id ?: error("Player1 not defined")
        val player2Id = currentGame.player2?.id ?: error("Player2 not defined")
        return player1Id to player2Id
    }
}