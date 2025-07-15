package com.lmar.checkersgame.presentation.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme
import com.lmar.checkersgame.domain.model.User
import com.lmar.checkersgame.presentation.common.components.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    topPlayers: List<User>
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
                    //onEvent(AuthEvent.ToBack)
                },
                state = rememberTopAppBarState()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                itemsIndexed(topPlayers) { index, player ->
                    RankingItem(index + 1, player)
                }
            }
        }
    }
}

@Composable
fun RankingItem(position: Int, user: User) {
    val backgroundColor = if (position % 2 == 0) Color.LightGray else Color.Gray
    var textColor = if (position % 2 == 0) Color.Gray else Color.White

    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text("$position. ", color = textColor)
            Text(user.names, modifier = Modifier.weight(1f), color = textColor)
            Text("${user.score} pts", color = textColor, fontWeight = FontWeight.Bold)
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