package com.lmar.checkersgame.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lmar.checkersgame.R
import com.lmar.checkersgame.core.ui.theme.CheckersGameTheme
import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.presentation.common.components.GlowingCard
import com.lmar.checkersgame.presentation.common.components.ShadowText
import com.lmar.checkersgame.presentation.common.event.AuthEvent
import com.lmar.checkersgame.presentation.common.state.AuthState
import com.lmar.checkersgame.presentation.common.viewmodel.auth.AuthViewModel
import com.lmar.checkersgame.presentation.navigation.handleUiEvents
import com.lmar.checkersgame.presentation.ui.event.HomeEvent
import com.lmar.checkersgame.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreenContainer(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus()

        navController.handleUiEvents(
            scope = coroutineScope,
            uiEventFlow = homeViewModel.eventFlow
        )
    }

    HomeScreen(
        authState,
        onEvent = {
            homeViewModel.onEvent(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    authState: AuthState,
    onEvent: (HomeEvent) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("")
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(HomeEvent.ToProfile) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Configuraciones",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
                    rememberTopAppBarState()
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShadowText(
                    text = stringResource(R.string.app_name),
                    fontFamily = MaterialTheme.typography.displayLarge.fontFamily!!,
                    fontSize = 32.sp,
                    textColor = MaterialTheme.colorScheme.primary,
                    shadowColor = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                GlowingCard(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(5.dp),
                    glowingColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primary,
                    cornerRadius = Int.MAX_VALUE.dp
                ) {
                    Image(
                        painter = painterResource(R.drawable.checkers),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        showBottomSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Un Jugador")
                }

                Spacer(modifier = Modifier.size(4.dp))

                if (authState.isAuthenticated) {
                    Button(
                        onClick = { onEvent(HomeEvent.ToRoom) },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text("Multijugador")
                    }

                    Spacer(modifier = Modifier.size(4.dp))

                    Button(
                        onClick = { onEvent(HomeEvent.ToRanking) },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text("Ranking")
                    }

                    Spacer(modifier = Modifier.size(4.dp))
                }
            }

        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShadowText(
                    text = "Selecciona Nivel",
                    fontFamily = MaterialTheme.typography.displayLarge.fontFamily!!,
                    fontSize = 24.sp,
                    textColor = MaterialTheme.colorScheme.primary,
                    shadowColor = MaterialTheme.colorScheme.primary
                )

                BottomSheetItem(title = "Fácil") {
                    showBottomSheet = false
                    onEvent(HomeEvent.ToSingleGame(Difficulty.EASY))
                }

                BottomSheetItem(title = "Normal") {
                    showBottomSheet = false
                    onEvent(HomeEvent.ToSingleGame(Difficulty.MEDIUM))
                }

                BottomSheetItem(title = "Difícil") {
                    showBottomSheet = false
                    onEvent(HomeEvent.ToSingleGame(Difficulty.HARD))
                }
            }
        }
    }
}

@Composable
fun BottomSheetItem(
    title: String,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CheckersGameTheme {
        HomeScreen(AuthState())
    }
}