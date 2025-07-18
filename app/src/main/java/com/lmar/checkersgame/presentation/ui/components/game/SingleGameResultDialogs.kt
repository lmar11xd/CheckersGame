package com.lmar.checkersgame.presentation.ui.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.presentation.ui.event.GameEvent
import com.lmar.checkersgame.presentation.ui.state.GameState

@Composable
fun SingleGameResultDialogs(
    gameState: GameState,
    userId: String,
    showExitDialog: Boolean,
    onDismissExitDialog: () -> Unit,
    onEvent: (GameEvent) -> Unit
) {
    val game = gameState.game

    if (game.status == GameStatusEnum.FINISHED) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = when (game.winner) {
                        null -> "Empate"
                        userId -> "¡Felicidades, ganaste!"
                        else -> "Perdiste"
                    },
                    fontSize = 20.sp
                )
            },
            text = { Text("¿Qué deseas hacer?") },
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

    if (game.status == GameStatusEnum.ABORTED && game.winner == userId) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("¡Felicidades, ganaste!") },
            text = { Text("Tu oponente abandonó la partida.") },
            confirmButton = {
                TextButton(onClick = { onEvent(GameEvent.ToBack) }) {
                    Text("Salir")
                }
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = onDismissExitDialog,
            title = { Text("¿Deseas salir de la partida?") },
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
                    onDismissExitDialog()
                    onEvent(GameEvent.ToBack)
                }) {
                    Text("Salir")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissExitDialog) {
                    Text("Cancelar")
                }
            }
        )
    }
}