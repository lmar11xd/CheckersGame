package com.lmar.checkersgame.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme
import com.lmar.checkersgame.domain.model.User
import com.lmar.checkersgame.presentation.common.components.AppBar
import com.lmar.checkersgame.presentation.common.components.GradientCard
import com.lmar.checkersgame.presentation.navigation.handleUiEvents
import com.lmar.checkersgame.presentation.ui.event.RankingEvent
import com.lmar.checkersgame.presentation.viewmodel.RankingViewModel

@Composable
fun RankingScreenContainer(
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()

    val viewModel: RankingViewModel = hiltViewModel()
    val topPlayers by viewModel.topPlayers.collectAsState()

    LaunchedEffect(Unit) {
        navController.handleUiEvents(
            coroutineScope,
            viewModel.eventFlow
        )
    }

    RankingScreen(
        topPlayers,
        onEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankingScreen(
    topPlayers: List<User>,
    onEvent: (RankingEvent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.bg1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column {
            AppBar(
                "Ranking",
                onBackAction = {
                    onEvent(RankingEvent.ToBack)
                },
                state = rememberTopAppBarState()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                itemsIndexed(topPlayers) { index, player ->
                    RankingItem(
                        modifier = Modifier.padding(vertical = 4.dp),
                        position = index + 1,
                        user = player
                    )
                }
            }
        }
    }
}

@Composable
fun RankingItem(modifier: Modifier = Modifier, position: Int, user: User) {
    GradientCard(modifier) {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text("$position. ", color = Color.White)
            Text(user.names, modifier = Modifier.weight(1f), color = Color.White)
            Text("${user.score} pts", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RankingScreenPreview() {
    val users = listOf<User>(
        User(id = "01", names = "User 1", score = 1000),
        User(id = "02", names = "User 2", score = 800),
        User(id = "03", names = "User 3", score = 600),
        User(id = "04", names = "User 4", score = 550),
        User(id = "05", names = "User 5", score = 400),
        User(id = "06", names = "User 6", score = 200)
    )

    CheckersGameTheme {
        RankingScreen(users)
    }
}