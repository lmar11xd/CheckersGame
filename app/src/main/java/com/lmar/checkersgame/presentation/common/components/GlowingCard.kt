package com.lmar.checkersgame.presentation.common.components

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    glowingColor: Color,
    containerColor: Color = Color.White,
    cornerRadius: Dp = 20.dp,
    glowingRadius: Dp = 20.dp,
    xShifting: Dp = 0.dp,
    yShifting: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.drawBehind {
            val size = this.size
            drawContext.canvas.nativeCanvas.apply {
                drawRoundRect(
                    0f,
                    0f,
                    size.width,
                    size.height,
                    cornerRadius.toPx(),
                    cornerRadius.toPx(),
                    Paint().apply {
                        color = containerColor.toArgb()
                        setShadowLayer(
                            glowingRadius.toPx(),
                            xShifting.toPx(),
                            yShifting.toPx(),
                            glowingColor.toArgb()//glowingColor.copy(alpha = 0.5f).toArgb()
                        )
                    }
                )
            }
        }
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun GlowingCardPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            GlowingCard(
                modifier = Modifier.size(200.dp),
                glowingColor = Color.Green,
                cornerRadius = Int.MAX_VALUE.dp
            ) {
                Text(text = "Content", modifier = Modifier.align(Alignment.Center))
            }

            Box(modifier = Modifier.height(40.dp))

            GlowingCard(
                modifier = Modifier.size(100.dp),
                glowingColor = Color.Green,
                cornerRadius = 10.dp,
                containerColor = Color.Cyan,
                xShifting = 10.dp
            ) {
                Text(text = "Content", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}