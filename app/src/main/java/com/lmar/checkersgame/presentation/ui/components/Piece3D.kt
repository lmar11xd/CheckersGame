package com.lmar.checkersgame.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Piece3D(
    modifier: Modifier = Modifier,
    baseColor: Color = Color.Red,
    shadowColor: Color = Color.Black,
    highlightColor: Color = Color.White,
    isKing: Boolean = false
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Sombra exterior
            /*drawCircle(
                color = shadowColor.copy(alpha = 0.3f),
                radius = radius + 8f,
                center = center + Offset(4f, 4f)
            )*/

            // Cuerpo con gradiente radial
            drawCircle(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(highlightColor, baseColor),
                    center = center - Offset(10f, 10f),
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Contorno suave
            drawCircle(
                color = shadowColor.copy(alpha = 0.6f),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
        }

        if (isKing) {
            NeonCrownIcon(
                modifier = Modifier
                    .size(18.dp) // tamaño fijo razonable
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Piece3DPreview() {
    Piece3D(
        modifier = Modifier.size(36.dp),
        baseColor = Color.Red,
        isKing = true
    )
}
