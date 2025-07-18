package com.lmar.checkersgame.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.logic.generateInitialBoard
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.presentation.common.components.AppBar
import com.lmar.checkersgame.presentation.navigation.handleUiEvents
import com.lmar.checkersgame.presentation.ui.components.game.GameBoard
import com.lmar.checkersgame.presentation.ui.components.game.GameHeaderInfo
import com.lmar.checkersgame.presentation.ui.components.game.GameResultDialog
import com.lmar.checkersgame.presentation.ui.event.GameEvent
import com.lmar.checkersgame.presentation.ui.state.GameState
import com.lmar.checkersgame.presentation.viewmodel.GameViewModel

@Composable
fun GameScreenContainer(
    navController: NavHostController,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val userId = viewModel.userId

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        navController.handleUiEvents(
            coroutineScope,
            viewModel.eventFlow
        )
    }

    GameScreen(
        gameState = gameState,
        onEvent = { viewModel.onEvent(it) },
        userId = userId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameScreen(
    gameState: GameState = GameState(),
    onEvent: (GameEvent) -> Unit = {},
    userId: String
) {
    var showExitDialog by remember { mutableStateOf(false) }

    val isUserTurn = gameState.game.turn == userId
    val opponentName =
        (if (gameState.game.player1?.id == userId) gameState.game.player2?.name
        else gameState.game.player1?.name) ?: ""

    BackHandler {
        showExitDialog = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column {
            AppBar(
                stringResource(R.string.app_name),
                onBackAction = {
                    showExitDialog = true
                },
                state = rememberTopAppBarState()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                GameHeaderInfo(
                    gameTime = gameState.game.gameTime,
                    title = "Puntos",
                    label = (gameState.scores[userId] ?: 0).toString(),
                    isUserTurn = isUserTurn,
                    opponentName = opponentName
                )

                if(gameState.game.board.isNotEmpty()) {
                    GameBoard(
                        board = gameState.game.board,
                        selectedCell = gameState.selectedCell,
                        userId = userId,
                        gameStatus = gameState.game.status,
                        onCellClick = { row, col -> onEvent(GameEvent.CellClicked(row, col)) }
                    )
                }

                Text(
                    text = gameState.room.roomCode,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.End)
                )

                GameResultDialog(
                    gameState = gameState,
                    userId = userId,
                    showExitDialog = showExitDialog,
                    onDismissExitDialog = { showExitDialog = false },
                    onEvent = onEvent
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    CheckersGameTheme {
        val board = generateInitialBoard("01", "02")
        val game = Game(
            "12345",
            Player("01", "Player 1"),
            Player("02", "Player 2"),
            board, "01", "01",
            GameStatusEnum.PLAYING
        )
        val room = Room("0001", "908060", RoomStatusEnum.COMPLETED)

        GameScreen(
            gameState = GameState(game = game, room = room),
            userId = "01"
        )
    }
}