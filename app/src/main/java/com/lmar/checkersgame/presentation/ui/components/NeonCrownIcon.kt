package com.lmar.checkersgame.presentation.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun NeonCrownIcon(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Yellow,
    iconSize: Int = 18
) {
    val transition = rememberInfiniteTransition(label = "GlowPulse")
    val glowRadius by transition.animateFloat(
        initialValue = 10f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowRadius"
    )

    val painter = rememberVectorPainter(image = NeonCrown)

    val scale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Canvas(
        modifier = modifier
            .size(iconSize.dp)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = Color.Transparent
                        this.asFrameworkPaint().apply {
                            isAntiAlias = true
                            setShadowLayer(
                                glowRadius,
                                0f,
                                0f,
                                glowColor.copy(alpha = 0.3f).toArgb()
                            )
                        }
                    }

                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // ✅ Aquí se dibuja directamente sobre el canvas nativo
                    canvas.nativeCanvas.drawCircle(cx, cy, size.minDimension / 2f, paint.asFrameworkPaint())
                }
            }
    ) {
        scale(scale, scale, pivot = Offset(size.width / 2f, size.height / 2f)) {
            translate(
                left = (size.width - iconSize.dp.toPx()) / 2f,
                top = (size.height - iconSize.dp.toPx()) / 2f
            ) {
                with(painter) {
                    draw(size = this@Canvas.size)
                }
            }
        }
    }
}