package com.lmar.checkersgame.presentation.ui.components

import androidx.compose.runtime.Composable

@Composable
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}