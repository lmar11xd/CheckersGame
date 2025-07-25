package com.lmar.checkersgame.presentation.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme
import com.lmar.checkersgame.presentation.common.components.AppBar
import com.lmar.checkersgame.presentation.common.components.GradientButton
import com.lmar.checkersgame.presentation.common.components.GradientCard
import com.lmar.checkersgame.presentation.navigation.handleUiEvents
import com.lmar.checkersgame.presentation.ui.components.RoomTextField
import com.lmar.checkersgame.presentation.ui.event.RoomEvent
import com.lmar.checkersgame.presentation.ui.state.RoomState
import com.lmar.checkersgame.presentation.viewmodel.RoomViewModel

@Composable
fun RoomScreenContainer(
    navController: NavHostController,
    roomViewModel: RoomViewModel = hiltViewModel()
) {
    val roomState by roomViewModel.roomState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        navController.handleUiEvents(
            coroutineScope,
            roomViewModel.eventFlow,
        )
    }

    RoomScreen(
        roomState,
        onEvent = {
            roomViewModel.onEvent(it)
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomScreen(
    roomState: RoomState = RoomState(),
    onEvent: (RoomEvent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column {
            AppBar(
                "Multijugador",
                onBackAction = { onEvent(RoomEvent.ToBack) },
                state = rememberTopAppBarState()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(100.dp))

                GradientCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 30.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Unirse a Partida",
                            fontWeight = FontWeight.Bold,
                            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RoomTextField(
                                value = roomState.roomCode,
                                onValueChange = { onEvent(RoomEvent.EnteredRoomCode(it)) },
                                keyboardType = KeyboardType.Number,
                                label = "Código de Partida",
                                modifier = Modifier.weight(1f)
                            )

                            GradientButton(
                                "Ir",
                                onClick = {
                                    onEvent(RoomEvent.JoinRoom)
                                }
                            )
                        }
                    }
                }

                GradientCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Crear Partida",
                            fontWeight = FontWeight.Bold,
                            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        GradientButton(
                            "Jugar",
                            onClick = {
                                onEvent(RoomEvent.CreateRoom)
                            }
                        )
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun RoomScreenPreview() {
    CheckersGameTheme {
        RoomScreen()
    }
}