package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.ai.AIPlayer
import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.usecase.single_game.InitializeSingleGameUseCase
import com.lmar.checkersgame.domain.usecase.single_game.MovePieceUseCase
import com.lmar.checkersgame.domain.usecase.single_game.ResetSingleGameUseCase
import com.lmar.checkersgame.domain.usecase.StartGameTimerUseCase
import com.lmar.checkersgame.domain.usecase.StopGameTimerUseCase
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
class SingleGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val initializeGame: InitializeSingleGameUseCase,
    private val startTimer: StartGameTimerUseCase,
    private val stopTimer: StopGameTimerUseCase,
    private val movePiece: MovePieceUseCase,
    private val resetGameUseCase: ResetSingleGameUseCase
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var aiPlayer: AIPlayer? = null
    private var timerJob: Job? = null

    var userId = ""
    var gameLevel = Difficulty.EASY

    init {
        savedStateHandle.get<String>("level")?.let { level ->
            gameLevel = Difficulty.valueOf(level)
            viewModelScope.launch {
                initializeGame(level) { game, id, ai, isAuth->
                    userId = id
                    aiPlayer = ai
                    _gameState.value = _gameState.value.copy(game = game, isAuthenticated = isAuth)
                    startGameTimer()
                }
            }
        }
    }

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.CellClicked -> {
                onCellClick(event.row, event.col)
            }

            GameEvent.Rematch -> {
                resetGame()
            }

            GameEvent.ToBack -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToBack)
                }
            }

            GameEvent.AbortGame -> {
            }

            GameEvent.LeaveRoom -> {
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
        val current = _gameState.value.selectedCell
        val pos = row to col
        val game = _gameState.value.game
        val piece = game.board[row][col]

        if (piece.playerId == userId && game.turn == userId) {
            _gameState.value = _gameState.value.copy(selectedCell = pos)
        } else if (current != null && current != pos) {
            moveIfValid(current, pos)
        } else {
            _gameState.value = _gameState.value.copy(selectedCell = null)
        }
    }

    fun moveIfValid(from: Position, to: Position) {
        val game = _gameState.value.game
        if (game.turn != userId) return

        viewModelScope.launch {
            movePiece(
                from = from,
                to = to,
                game = game,
                userId = userId,
                isAuthenticated = _gameState.value.isAuthenticated,
                updateGame = { _gameState.value = _gameState.value.copy(game = it) },
                updateSelected = { _gameState.value = _gameState.value.copy(selectedCell = it) },
                declareWinner = { winner ->
                    if (winner != null) {
                        stopGameTimer()
                        _gameState.value = _gameState.value.copy(
                            game = _gameState.value.game.copy(
                                winner = winner,
                                status = GameStatusEnum.FINISHED
                            )
                        )
                    }
                }
            )
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            val game = resetGameUseCase(userId)
            _gameState.value = _gameState.value.copy(game = game)
            startGameTimer()
        }
    }
}
