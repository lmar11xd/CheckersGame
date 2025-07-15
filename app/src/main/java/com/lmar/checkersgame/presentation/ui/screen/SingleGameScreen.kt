package com.lmar.checkersgame.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.logic.generateInitialBoard
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.domain.model.isNotEmpty
import com.lmar.checkersgame.presentation.common.components.AppBar
import com.lmar.checkersgame.presentation.navigation.handleUiEvents
import com.lmar.checkersgame.presentation.ui.components.Piece3D
import com.lmar.checkersgame.presentation.ui.components.formatTime
import com.lmar.checkersgame.presentation.ui.event.GameEvent
import com.lmar.checkersgame.presentation.ui.state.GameState
import com.lmar.checkersgame.presentation.viewmodel.SingleGameViewModel

@Composable
fun SingleGameScreenContainer(
    navController: NavHostController,
    singleGameViewModel: SingleGameViewModel = hiltViewModel()
) {
    val gameState by singleGameViewModel.gameState.collectAsState()
    val userId = singleGameViewModel.userId

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        navController.handleUiEvents(
            scope = coroutineScope,
            uiEventFlow = singleGameViewModel.eventFlow
        )
    }

    SingleGameScreen(
        gameState = gameState,
        userId = userId,
        onEvent = {
            singleGameViewModel.onEvent(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleGameScreen(
    gameState: GameState = GameState(),
    userId: String,
    onEvent: (GameEvent) -> Unit = {}
) {
    var showExitDialog by remember { mutableStateOf(false) }

    val isUserTurn = gameState.game.turn == userId

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
                    .padding(8.dp)
            ) {
                val oponentName =
                    (if (gameState.game.player1?.id == userId) gameState.game.player2?.name
                    else gameState.game.player1?.name) ?: ""

                // Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.width(120.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 4.dp),
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Tiempo",
                            tint = Color.DarkGray
                        )
                        Text(
                            formatTime(gameState.game.gameTime),
                            color = Color.DarkGray,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(Color.LightGray, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                "Nivel",
                                color = Color.DarkGray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                gameState.gameLevel.value,
                                color = Color.DarkGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.width(120.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = if (isUserTurn) "Tu turno" else "Turno de $oponentName",
                            fontSize = 12.sp,
                            textAlign = TextAlign.End,
                            color = Color.DarkGray
                        )
                    }
                }

                // Tablero
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.border(4.dp, Color.Black)
                    ) {
                        for (row in 0 until 8) {
                            Row(modifier = Modifier.weight(1f)) {
                                for (col in 0 until 8) {
                                    val piece = gameState.game.board[row][col]
                                    val isDark = (row + col) % 2 == 1
                                    val isSelected = gameState.selectedCell == (row to col)

                                    var borderColor = Color.Black
                                    var borderWith = 1.dp
                                    if (isSelected) {
                                        borderColor = Color.Green
                                        borderWith = 2.dp
                                    }

                                    var paddingTop = if (row == 0) 4.dp else 0.dp
                                    var paddingBottom = if (row == 7) 4.dp else 0.dp
                                    var paddingStart = if (col == 0) 4.dp else 0.dp
                                    var paddingEnd = if (col == 7) 4.dp else 0.dp

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(
                                                when {
                                                    isDark -> Color(0xFF4E342E)
                                                    else -> Color(0xFFFFF8E1)
                                                }
                                            )
                                            .padding(
                                                start = paddingStart,
                                                top = paddingTop,
                                                bottom = paddingBottom,
                                                end = paddingEnd
                                            )
                                            .border(borderWith, borderColor)
                                            .clickable {
                                                if (gameState.game.status == GameStatusEnum.PLAYING) {
                                                    onEvent(GameEvent.CellClicked(row, col))
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (piece.isNotEmpty() == true) {
                                            Piece3D(
                                                modifier = Modifier.size(36.dp),
                                                baseColor = if (piece.playerId == userId) Color.Red else Color.Black,
                                                isKing = piece.isKing
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (gameState.game.status == GameStatusEnum.FINISHED) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = {
                            Text(
                                text = when (gameState.game.winner) {
                                    null -> "Empate"
                                    userId -> "¡Felicidades, ganaste!"
                                    else -> "Perdiste"
                                },
                                fontSize = 20.sp
                            )
                        },
                        text = {
                            Text("¿Qué deseas hacer?")
                        },
                        confirmButton = {
                            TextButton(onClick = { onEvent(GameEvent.Rematch) }) {
                                Text("Revancha")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { onEvent(GameEvent.ToBack) }) {
                                Text("Salir")
                            }
                        }
                    )
                }

                if (gameState.game.status == GameStatusEnum.ABORTED && gameState.game.winner == userId) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = {
                            Text(text = "¡Felicidades, ganaste!")
                        },
                        text = {
                            Text("Tu oponente abandonó la partida.")
                        },
                        confirmButton = {
                            TextButton(onClick = { onEvent(GameEvent.ToBack) }) {
                                Text("Salir")
                            }
                        }
                    )
                }

                if (showExitDialog) {
                    AlertDialog(
                        onDismissRequest = { showExitDialog = false },
                        title = {
                            Text("¿Deseas salir de la partida?")
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("La partida se dará por terminada.")
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showExitDialog = false
                                onEvent(GameEvent.ToBack)
                            }) {
                                Text("Salir")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showExitDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun SingleGameScreenPreview() {
    CheckersGameTheme {
        val board = generateInitialBoard("01", "02")
        val game = Game(
            "12345",
            Player("01", "Player 1"),
            Player("02", "Player 2"),
            board, "01", "01",
            GameStatusEnum.PLAYING
        )

        SingleGameScreen(
            GameState(game),
            "01"
        )
    }
}