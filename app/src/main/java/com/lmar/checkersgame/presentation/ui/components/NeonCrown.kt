package com.lmar.checkersgame.presentation.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val NeonCrown: ImageVector
    get() = ImageVector.Builder(
        name = "NeonCrown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Yellow),
            fillAlpha = 1.0f,
            stroke = null,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(5f, 16f)
            lineTo(3f, 7f)
            lineTo(8f, 10f)
            lineTo(12f, 4f)
            lineTo(16f, 10f)
            lineTo(21f, 7f)
            lineTo(19f, 16f)
            close()

            moveTo(4f, 18f)
            lineTo(20f, 18f)
            lineTo(20f, 20f)
            lineTo(4f, 20f)
            close()
        }
    }.build()
