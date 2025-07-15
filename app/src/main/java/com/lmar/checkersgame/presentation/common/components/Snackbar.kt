package com.lmar.checkersgame.presentation.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

enum class SnackbarType {
    SUCCESS, ERROR, INFO, WARN
}

data class SnackbarEvent(
    val message: String,
    val type: SnackbarType = SnackbarType.INFO
)

fun getSnackbarColor(type: SnackbarType): Color = when (type) {
    SnackbarType.SUCCESS -> Color(0xFF4CAF50) // Verde
    SnackbarType.ERROR -> Color(0xFFF44336)   // Rojo
    SnackbarType.INFO -> Color(0xFF2196F3)    // Azul
    SnackbarType.WARN -> Color(0xFFFFC107)    // Amarillo
}

fun getSnackbarIcon(type: SnackbarType): ImageVector = when (type) {
    SnackbarType.SUCCESS -> Icons.Default.CheckCircle
    SnackbarType.ERROR -> Icons.Default.Error
    SnackbarType.INFO -> Icons.Default.Info
    SnackbarType.WARN -> Icons.Default.Warning
}

object SnackbarManager {
    private val snackbarChannel = Channel<SnackbarEvent>()
    val snackbarFlow = snackbarChannel.receiveAsFlow()

    suspend fun showMessage(message: String, type: SnackbarType = SnackbarType.INFO) {
        snackbarChannel.send(SnackbarEvent(message, type))
    }
}

@Composable
fun Snackbar(snackbarData: SnackbarData, type: SnackbarType) {
    val backgroundColor = getSnackbarColor(type)
    val icon = getSnackbarIcon(type)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        shadowElevation = 6.dp,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = snackbarData.visuals.message,
                color = Color.White
            )
        }
    }
}