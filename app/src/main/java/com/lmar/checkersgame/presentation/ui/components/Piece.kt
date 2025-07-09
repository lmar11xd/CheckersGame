package com.lmar.checkersgame.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lmar.checkersgame.domain.model.Piece

@Composable
fun PieceItem(
    piece: Piece,
    currentUserId: String,
    size: Dp = 40.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        drawCircle(
            color = if (piece.playerId == currentUserId) Color.Red else Color.Black
        )
    }

    if (piece.isKing) {
        NeonCrownIcon(glowColor = Color.Transparent, iconSize = 25)
    }
}