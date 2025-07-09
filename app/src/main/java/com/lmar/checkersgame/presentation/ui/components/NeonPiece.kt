package com.lmar.checkersgame.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.isEmpty

@Composable
fun NeonPiece(
    piece: Piece,
    currentUserId: String,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    playerColor: Color = if (piece.playerId == currentUserId) Color.Red else Color.Black,
    glowColor: Color = if (piece.playerId == currentUserId) Color.Magenta else Color.Cyan
) {
    if (piece.isEmpty()) return

    val transition = rememberInfiniteTransition(label = "PiecePulse")
    val glowRadius by transition.animateFloat(
        initialValue = 8f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowRadius"
    )

    Box(
        modifier = Modifier.padding(2.dp)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val radiusPx = size.toPx() / 2f
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

            // Glow/shadow
            drawIntoCanvas {
                val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = glowColor.copy(alpha = 0.8f).toArgb()
                    setShadowLayer(glowRadius, 0f, 0f, color)
                }
                it.nativeCanvas.drawCircle(center.x, center.y, radiusPx, paint)
            }

            // Pieza principal
            drawCircle(
                color = playerColor,
                radius = radiusPx,
                center = center
            )
        }

        // Corona si es rey
        if (piece.isKing) {
            NeonCrownIcon(
                modifier = Modifier
                    .size(size / 2),
                glowColor = Color.Yellow,
                iconSize = (size.value / 2).toInt()
            )
        }
    }
}