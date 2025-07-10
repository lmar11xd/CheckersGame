package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.ai.AIPlayer
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.logic.canContinueJumping
import com.lmar.checkersgame.domain.logic.generateInitialBoard
import com.lmar.checkersgame.domain.logic.hasAnyValidMoves
import com.lmar.checkersgame.domain.logic.hasPieces
import com.lmar.checkersgame.domain.logic.isValidMove
import com.lmar.checkersgame.domain.logic.shouldBeKing
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.sound.ISoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleGameViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val soundPlayer: ISoundPlayer,
    private val aiPlayer: AIPlayer
) : ViewModel() {

    private val _gameState = MutableStateFlow<Game?>(null)
    val gameState: StateFlow<Game?> = _gameState

    private val _gameTime = MutableStateFlow(0) // en segundos
    val gameTime: StateFlow<Int> = _gameTime

    private val _selectedCell = MutableStateFlow<Position?>(null)
    val selectedCell: StateFlow<Position?> = _selectedCell

    private val _winner = MutableStateFlow<String?>(null)
    val winner: StateFlow<String?> = _winner

    var userId = ""

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
        viewModelScope.launch {
            userId = authRepository.getCurrentUser()?.uid.toString()
            val player1 = Player(id = userId, name = "Tú")
            val player2 = Player(id = "AI", name = "IA")

            val initialBoard = generateInitialBoard(player1.id, player2.id)
            _gameState.value = Game(
                board = initialBoard,
                player1 = player1,
                player2 = player2,
                turn = player1.id,
                status = GameStatusEnum.PLAYING
            )

            startGameTimer()
        }

    }

    fun onCellClick(row: Int, col: Int) {
        val current = _selectedCell.value
        val pos = row to col
        val game = _gameState.value ?: return
        val piece = game.board[row][col]

        if (piece.playerId == userId && game.turn == userId) {
            _selectedCell.value = pos
        } else if (current != null && current != pos) {
            moveIfValid(current, pos)
        } else {
            _selectedCell.value = null
        }
    }

    fun moveIfValid(from: Position, to: Position) {
        val game = _gameState.value ?: return
        if (game.turn != userId) return
        val (player1Id, player2Id) = requirePlayerIds()

        val piece = game.board[from.first][from.second]
        if (piece.playerId != userId) return

        if (!isValidMove(game.board, from, to, userId, player1Id, player2Id)) return

        val newBoard = game.board.map { row -> row.map { it.copy() }.toMutableList() }.toMutableList()

        val rowDiff = to.first - from.first
        val colDiff = to.second - from.second
        val jumped = kotlin.math.abs(rowDiff) == 2 && kotlin.math.abs(colDiff) == 2

        newBoard[to.first][to.second] = piece.copy(
            isKing = piece.isKing || shouldBeKing(piece, to.first, player1Id, player2Id)
        )
        newBoard[from.first][from.second] = Piece()

        if (jumped) {
            val midRow = (from.first + to.first) / 2
            val midCol = (from.second + to.second) / 2
            newBoard[midRow][midCol] = Piece()
            soundPlayer.playCapture()
        } else {
            soundPlayer.playMove()
        }

        _gameState.value = game.copy(board = newBoard)

        if (jumped && canContinueJumping(newBoard, to, userId, player1Id, player2Id)) {
            _selectedCell.value = to
        } else {
            _selectedCell.value = null
            checkWinner(newBoard)
            if (_winner.value == null) {
                viewModelScope.launch {
                    makeAIMove(newBoard)
                }
            }
        }
    }

    private suspend fun makeAIMove(board: List<MutableList<Piece>>) {
        val (player1Id, player2Id) = requirePlayerIds()
        var newBoard = board
        do {
            val move = aiPlayer.getNextMove(newBoard, player2Id, player1Id) ?: break
            val (from, to) = move

            // Esperar antes de mover (ej. 500ms)
            delay(500)

            val piece = newBoard[from.first][from.second]
            newBoard = newBoard.map { row -> row.map { it.copy() }.toMutableList() }.toMutableList()

            val rowDiff = to.first - from.first
            val colDiff = to.second - from.second
            val jumped = kotlin.math.abs(rowDiff) == 2 && kotlin.math.abs(colDiff) == 2

            newBoard[to.first][to.second] = piece.copy(
                isKing = piece.isKing || shouldBeKing(piece, to.first, player1Id, player2Id)
            )
            newBoard[from.first][from.second] = Piece()

            if (jumped) {
                val midRow = (from.first + to.first) / 2
                val midCol = (from.second + to.second) / 2
                newBoard[midRow][midCol] = Piece()
                soundPlayer.playCapture()
            } else {
                soundPlayer.playMove()
            }

            _gameState.value = _gameState.value?.copy(board = newBoard)

        } while (jumped && canContinueJumping(newBoard, to, player2Id, player1Id, player2Id))

        // Pequeña pausa final para simular decisión de la IA
        delay(300)
        _gameState.value = _gameState.value?.copy(turn = userId)
        checkWinner(newBoard)
    }

    private fun checkWinner(board: List<MutableList<Piece>>) {
        val (player1Id, player2Id) = requirePlayerIds()

        val aiHasPieces = hasPieces(board, player2Id)
        val aiCanMove = hasAnyValidMoves(board, player2Id, player1Id, player2Id)
        val userHasPieces = hasPieces(board, player1Id)
        val userCanMove = hasAnyValidMoves(board, player1Id, player1Id, player2Id)

        if (!aiHasPieces || !aiCanMove) {
            _winner.value = player1Id
            _gameState.value = _gameState.value?.copy(winner = player1Id, status = GameStatusEnum.FINISHED)
            stopGameTimer()
        } else if (!userHasPieces || !userCanMove) {
            _winner.value = player2Id
            _gameState.value = _gameState.value?.copy(winner = player2Id, status = GameStatusEnum.FINISHED)
            stopGameTimer()
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            val player1 = Player(id = userId, name = "Tú")
            val player2 = Player(id = "AI", name = "IA")
            val initialBoard = generateInitialBoard(player1.id, player2.id)

            _gameState.value = Game(
                board = initialBoard,
                player1 = player1,
                player2 = player2,
                turn = player1.id,
                status = GameStatusEnum.PLAYING
            )
            _selectedCell.value = null
            _winner.value = null
            _gameTime.value = 0
            startGameTimer()
        }
    }

    private fun requirePlayerIds(): Pair<String, String> {
        val currentGame = _gameState.value ?: error("Game not initialized")
        val player1Id = currentGame.player1?.id ?: error("Player1 not defined")
        val player2Id = currentGame.player2?.id ?: error("Player2 not defined")
        return player1Id to player2Id
    }
}