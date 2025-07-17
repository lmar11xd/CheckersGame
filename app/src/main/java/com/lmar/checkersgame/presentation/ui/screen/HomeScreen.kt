package com.lmar.checkersgame.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.lmar.checkersgame.core.utils.Constants.PHOTO_ICON_SIZE
import com.lmar.checkersgame.core.utils.Constants.PHOTO_SIZE
import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.presentation.common.components.AppBar
import com.lmar.checkersgame.presentation.common.components.GradientButton
import com.lmar.checkersgame.presentation.common.components.GradientCircleImage
import com.lmar.checkersgame.presentation.common.components.ImageCircle
import com.lmar.checkersgame.presentation.common.components.ShadowText
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
            AppBar(
                withBackButton = false,
                actions = {
                    if (authState.isAuthenticated) {
                        IconButton(
                            onClick = { onEvent(HomeEvent.ToProfile) }
                        ) {
                            if (authState.user.imageUrl.isNotEmpty()) {
                                ImageCircle(
                                    borderWidth = 1,
                                    imageUrl = authState.user.imageUrl,
                                    modifier = Modifier.size(PHOTO_ICON_SIZE)
                                )
                            } else {
                                ImageCircle(
                                    borderWidth = 1,
                                    painter = painterResource(R.drawable.default_avatar),
                                    modifier = Modifier.size(PHOTO_ICON_SIZE)
                                )
                            }
                        }
                    } else {
                        IconButton(
                            onClick = { onEvent(HomeEvent.ToProfile) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Configuraciones",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                //verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShadowText(
                    text = stringResource(R.string.app_name),
                    fontFamily = MaterialTheme.typography.displayLarge.fontFamily!!,
                    fontSize = 32.sp,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    shadowColor = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                GradientCircleImage(
                    image = painterResource(id = R.drawable.checkers),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageSize = PHOTO_SIZE,
                    strokeWidth = 6.dp
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(32.dp))

                GradientButton(
                    text = "Un Jugador",
                    onClick = { showBottomSheet = true },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(4.dp))

                if (authState.isAuthenticated) {
                    GradientButton(
                        text = "Multijugador",
                        onClick = { onEvent(HomeEvent.ToRoom) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.size(4.dp))

                    GradientButton(
                        text = "Ranking",
                        onClick = { onEvent(HomeEvent.ToRanking) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.size(4.dp))
                }

                Spacer(modifier = Modifier.size(32.dp))
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
        HomeScreen(AuthState(isAuthenticated = true))
    }
}