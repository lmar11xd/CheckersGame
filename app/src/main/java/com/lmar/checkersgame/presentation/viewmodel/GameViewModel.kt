package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.data.sound.SoundPlayerWrapper
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.domain.model.getFirstName
import com.lmar.checkersgame.domain.model.isNotEmpty
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.repository.IRoomRepository
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.domain.usecase.game.AbortGameUseCase
import com.lmar.checkersgame.domain.usecase.game.EndGameUseCase
import com.lmar.checkersgame.domain.usecase.game.MovePieceUseCase
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
    private val soundPlayer: SoundPlayerWrapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _gameState = MutableStateFlow<Game?>(null)
    val gameState: StateFlow<Game?> = _gameState

    private val _roomState = MutableStateFlow<Room?>(null)
    val roomState: StateFlow<Room?> = _roomState

    private val _gameTime = MutableStateFlow(0) // en segundos
    val gameTime: StateFlow<Int> = _gameTime

    private val _rematchRequested = MutableStateFlow(false)
    val rematchRequested: StateFlow<Boolean> = _rematchRequested

    private val _selectedCell = MutableStateFlow<Position?>(null)
    val selectedCell: StateFlow<Position?> = _selectedCell

    private val _scores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val scores: StateFlow<Map<String, Int>> = _scores

    var userId: String = ""
    var roomId: String = ""
    var gameId: String = ""

    private var timerJob: Job? = null

    private lateinit var movePieceUseCase: MovePieceUseCase
    private lateinit var endGameUseCase: EndGameUseCase
    private lateinit var abortGameUseCase: AbortGameUseCase

    init {
        savedStateHandle.get<String>("roomId")?.let { rid ->
            roomId = rid
            initializeGame()
        }
    }

    private fun initializeGame() {
        viewModelScope.launch {
            userId = authRepository.getCurrentUser()?.uid.toString()
            val user = userRepository.getUserById(userId) ?: return@launch
            val player = Player(userId, user.getFirstName())

            val room = roomRepository.getRoomById(roomId) ?: return@launch
            _roomState.value = room

            gameId = repository.createOrJoinGame(player, roomId)

            movePieceUseCase = MovePieceUseCase(
                repository,
                viewModelScope,
                userId,
                gameId,
                soundPlayer
            )

            endGameUseCase = EndGameUseCase(
                repository,
                userRepository,
                viewModelScope,
                gameId,
                soundPlayer
            )

            abortGameUseCase = AbortGameUseCase(
                repository,
                roomRepository,
                userRepository,
                viewModelScope,
                gameId,
                roomId
            )

            repository.listenToGame(gameId) { game ->
                _gameState.value = game
                _scores.value = game.scores
                when (game.status) {
                    GameStatusEnum.PLAYING -> startGameTimer()
                    GameStatusEnum.FINISHED, GameStatusEnum.ABORTED -> stopGameTimer()
                    else -> {}
                }
            }
        }
    }

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

    fun onCellClick(row: Int, col: Int) {
        val currentGame = _gameState.value ?: return

        val current = _selectedCell.value
        val pos = row to col
        val piece = currentGame.board[row][col]

        if (piece.isNotEmpty() && piece.playerId == userId && currentGame.turn == userId) {
            _selectedCell.value = pos
        } else if (current != null && current != pos) {
            movePieceUseCase.execute(
                currentGame,
                current,
                pos,
                onUpdate = {
                    _gameState.value = it
                    _scores.value = it.scores
                },
                onGameEnd = { winnerId ->
                    endGameUseCase.execute(
                        currentGame,
                        winnerId
                    ) { resetGame() }
                }
            )
        } else {
            _selectedCell.value = null
        }
    }

    fun resetGame() {
        val currentGame = _gameState.value ?: return
        val player1 = currentGame.player1 ?: return
        val player2 = currentGame.player2 ?: return

        _selectedCell.value = null

        viewModelScope.launch {
            val newGameId = repository.createGame(player1, player2, roomId)
            gameId = newGameId
            repository.clearRematchRequests(newGameId)
            repository.listenToGame(newGameId) {
                _gameState.value = it
                _scores.value = it.scores
            }
        }
    }

    fun abortGame() {
        val currentGame = _gameState.value ?: return
        abortGameUseCase.execute(currentGame, userId)
    }

    fun leaveRoom() {
        viewModelScope.launch {
            roomRepository.setRoomStatus(roomId, RoomStatusEnum.CLOSED)
        }
    }

    fun requestRematch() {
        _rematchRequested.value = true

        viewModelScope.launch {
            repository.requestRematch(gameId, userId)
        }
    }
}