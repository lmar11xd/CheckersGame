package com.lmar.checkersgame.presentation.ui.components.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.lmar.checkersgame.R
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.isNotEmpty
import com.lmar.checkersgame.presentation.common.components.GradientCard
import com.lmar.checkersgame.presentation.ui.components.Piece3D

@Composable
fun GameBoard(
    board: List<List<Piece>>,
    selectedCell: Pair<Int, Int>?,
    userId: String,
    gameStatus: GameStatusEnum,
    onCellClick: (Int, Int) -> Unit
) {
    GradientCard(
        cardRadius = 0.dp,
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .background(Color.White)
                .border(4.dp, Color.Transparent, shape = RoundedCornerShape(8.dp))
        ) {
            for (row in 0 until 8) {
                Row(modifier = Modifier.weight(1f)) {
                    for (col in 0 until 8) {
                        val piece = board[row][col]
                        val isDark = (row + col) % 2 == 1
                        val isSelected = selectedCell == (row to col)

                        val borderColor = if (isSelected) Color.Green else Color.DarkGray
                        val borderWidth = if (isSelected) 2.dp else 1.dp

                        val paddingModifier = Modifier.padding(
                            start = if (col == 0) 4.dp else 0.dp,
                            top = if (row == 0) 4.dp else 0.dp,
                            bottom = if (row == 7) 4.dp else 0.dp,
                            end = if (col == 7) 4.dp else 0.dp
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isDark) colorResource(R.color.green_light) else Color.White)
                                .then(paddingModifier)
                                .border(borderWidth, borderColor)
                                .clickable(enabled = gameStatus == GameStatusEnum.PLAYING) {
                                    onCellClick(row, col)
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
}