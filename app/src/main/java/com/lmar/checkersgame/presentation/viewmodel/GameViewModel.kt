package com.lmar.checkersgame.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.data.common.IAuthRepository
import com.lmar.checkersgame.data.common.IUserRepository
import com.lmar.checkersgame.data.repository.IGameRepository
import com.lmar.checkersgame.data.repository.IRoomRepository
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.logic.canContinueJumping
import com.lmar.checkersgame.domain.logic.generateInitialBoard
import com.lmar.checkersgame.domain.logic.hasPieces
import com.lmar.checkersgame.domain.logic.isValidMove
import com.lmar.checkersgame.domain.logic.shouldBeKing
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.domain.model.getFirstName
import com.lmar.checkersgame.domain.model.isNotEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _gameState = MutableLiveData<Game>()
    val gameState: LiveData<Game> = _gameState

    private val _roomState = MutableLiveData<Room>()
    val roomState: LiveData<Room> = _roomState

    var userId: String = ""
    var roomId: String = ""
    var gameId: String = ""

    private val _selectedCell = MutableStateFlow<Position?>(null)
    val selectedCell: StateFlow<Position?> = _selectedCell

    private val _winner = MutableStateFlow<String?>(null)
    val winner: StateFlow<String?> = _winner

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

        // Mover pieza
        newBoard[to.first][to.second] = piece.copy(
            isKing = piece.isKing || shouldBeKing(piece, to.first, player1Id, player2Id)
        )
        newBoard[from.first][from.second] = Piece() // Limpiar celda

        val rowDiff = to.first - from.first
        val colDiff = to.second - from.second
        val jumped = kotlin.math.abs(rowDiff) == 2 && kotlin.math.abs(colDiff) == 2

        if (jumped) {
            val midRow = (from.first + to.first) / 2
            val midCol = (from.second + to.second) / 2
            newBoard[midRow][midCol] = Piece()
        }

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
        _winner.value = winnerId
        viewModelScope.launch {
            repository.setWinner(gameId, winnerId)
            repository.setGameStatus(gameId, GameStatusEnum.FINISHED)
        }
    }

    fun resetGame() {
        val currentGame = _gameState.value ?: return

        val player1Id = currentGame.player1?.id.toString()
        val player2Id = currentGame.player2?.id.toString()

        val newBoard = generateInitialBoard(player1Id, player2Id)
        _gameState.value = currentGame.copy(
            board = newBoard,
            turn = userId
        )

        _selectedCell.value = null
        _winner.value = null

        viewModelScope.launch {
            repository.updateBoard(gameId, newBoard)
            repository.updateTurn(gameId, userId)
            repository.setGameStatus(gameId, GameStatusEnum.PLAYING)
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

    private fun requirePlayerIds(): Pair<String, String> {
        val currentGame = _gameState.value ?: error("Game not initialized")
        val player1Id = currentGame.player1?.id ?: error("Player1 not defined")
        val player2Id = currentGame.player2?.id ?: error("Player2 not defined")
        return player1Id to player2Id
    }
}