package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.data.sound.SoundPlayerWrapper
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.domain.model.getFirstName
import com.lmar.checkersgame.domain.model.isNotEmpty
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.repository.IRoomRepository
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.domain.usecase.StartGameTimerUseCase
import com.lmar.checkersgame.domain.usecase.StopGameTimerUseCase
import com.lmar.checkersgame.domain.usecase.game.AbortGameUseCase
import com.lmar.checkersgame.domain.usecase.game.EndGameUseCase
import com.lmar.checkersgame.domain.usecase.game.MovePieceUseCase
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.ui.event.GameEvent
import com.lmar.checkersgame.presentation.ui.state.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository,
    private val roomRepository: IRoomRepository,
    private val repository: IGameRepository,
    private val soundPlayer: SoundPlayerWrapper,
    private val startTimer: StartGameTimerUseCase,
    private val stopTimer: StopGameTimerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState>(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private val _roomState = MutableStateFlow<Room?>(null)
    val roomState: StateFlow<Room?> = _roomState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.CellClicked -> {
                onCellClick(event.row, event.col)
            }

            GameEvent.Rematch -> {
                requestRematch()
            }

            GameEvent.ToBack -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToBack)
                }
            }

            GameEvent.AbortGame -> {
                abortGame()
            }

            GameEvent.LeaveRoom -> {
                leaveRoom()
            }
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
                _gameState.value = _gameState.value.copy(game = game, scores = game.scores)
                when (game.status) {
                    GameStatusEnum.PLAYING -> startGameTimer()
                    GameStatusEnum.FINISHED, GameStatusEnum.ABORTED -> stopGameTimer()
                    else -> {}
                }
            }
        }
    }

    private fun startGameTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            startTimer(
                updateTimer = {
                    _gameState.value = _gameState.value.copy(
                        game = _gameState.value.game.copy(gameTime = it)
                    )
                },
                shouldContinue = { true }
            )
        }
    }

    private fun stopGameTimer() {
        stopTimer { timerJob?.cancel() }
    }

    fun onCellClick(row: Int, col: Int) {
        val currentGame = _gameState.value.game
        val current = _gameState.value.selectedCell
        val pos = row to col
        val piece = currentGame.board[row][col]

        if (piece.isNotEmpty() && piece.playerId == userId && currentGame.turn == userId) {
            _gameState.value = _gameState.value.copy(selectedCell = pos)
        } else if (current != null && current != pos) {
            movePieceUseCase.execute(
                currentGame,
                current,
                pos,
                onUpdate = {
                    _gameState.value = _gameState.value.copy(game = it, scores = it.scores)
                },
                updateSelected = {
                    _gameState.value = _gameState.value.copy(selectedCell = it)
                },
                onGameEnd = { winnerId ->
                    endGameUseCase.execute(
                        currentGame,
                        winnerId
                    ) { resetGame() }
                }
            )
        } else {
            _gameState.value = _gameState.value.copy(selectedCell = null)
        }
    }

    fun resetGame() {
        val currentGame = _gameState.value.game
        val player1 = currentGame.player1 ?: return
        val player2 = currentGame.player2 ?: return

        _gameState.value = _gameState.value.copy(selectedCell = null)

        viewModelScope.launch {
            val newGameId = repository.createGame(player1, player2, roomId)
            gameId = newGameId
            repository.clearRematchRequests(newGameId)
            repository.listenToGame(newGameId) {
                _gameState.value = _gameState.value.copy(game = it, scores = it.scores)
            }
        }
    }

    fun abortGame() {
        val currentGame = _gameState.value.game
        abortGameUseCase.execute(currentGame, userId)
        onEvent(GameEvent.ToBack)
    }

    fun leaveRoom() {
        viewModelScope.launch {
            roomRepository.setRoomStatus(roomId, RoomStatusEnum.CLOSED)
            onEvent(GameEvent.ToBack)
        }
    }

    fun requestRematch() {
        _gameState.value = _gameState.value.copy(rematchRequested = true)

        viewModelScope.launch {
            repository.requestRematch(gameId, userId)
        }
    }
}