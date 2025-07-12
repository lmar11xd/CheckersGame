package com.lmar.checkersgame.presentation.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.lmar.checkersgame.presentation.viewmodel.RankingViewModel
import androidx.compose.runtime.getValue

@Composable
fun RankingScreen(viewModel: RankingViewModel) {
    val topPlayers by viewModel.topPlayers.collectAsState()

    LazyColumn {
        itemsIndexed(topPlayers) { index, player ->
            Text(text = "${index + 1}. ${player.names} - ${player.score} pts")
        }
    }
}