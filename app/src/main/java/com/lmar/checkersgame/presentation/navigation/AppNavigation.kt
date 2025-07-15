package com.lmar.checkersgame.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.presentation.common.components.Snackbar
import com.lmar.checkersgame.presentation.common.components.SnackbarManager
import com.lmar.checkersgame.presentation.common.components.SnackbarType
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.common.ui.auth.LoginScreenContainer
import com.lmar.checkersgame.presentation.common.ui.auth.ProfileScreenContainer
import com.lmar.checkersgame.presentation.common.ui.auth.SignUpScreenContainer
import com.lmar.checkersgame.presentation.ui.screen.GameScreenContainer
import com.lmar.checkersgame.presentation.ui.screen.HomeScreenContainer
import com.lmar.checkersgame.presentation.ui.screen.RankingScreenContainer
import com.lmar.checkersgame.presentation.ui.screen.RoomScreenContainer
import com.lmar.checkersgame.presentation.ui.screen.SingleGameScreenContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf(SnackbarType.INFO) }

    LaunchedEffect(Unit) {
        // Escuchar mensajes del manager
        SnackbarManager.snackbarFlow.collect { event ->
            snackbarType = event.type
            snackbarHostState.showSnackbar(
                message = event.message,
                withDismissAction = true
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(snackbarData = data, type = snackbarType)
                }
            )
        }
    ) {
        NavHost(navController, startDestination = AppRoutes.HomeScreen.route) {
            composable(route = AppRoutes.LoginScreen.route) {
                LoginScreenContainer(navController)
            }

            composable(route = AppRoutes.SignUpScreen.route) {
                SignUpScreenContainer(navController)
            }

            composable(route = AppRoutes.ProfileScreen.route) {
                ProfileScreenContainer(navController)
            }

            composable(AppRoutes.HomeScreen.route) {
                HomeScreenContainer(navController)
            }

            composable(
                AppRoutes.SingleGameScreen.withArgs("level"),
                arguments = listOf(
                    navArgument("level") {
                        type = NavType.StringType
                        defaultValue = Difficulty.EASY.name
                    }
                )
            ) {
                SingleGameScreenContainer(navController)
            }

            composable(
                AppRoutes.GameScreen.withArgs("roomId"),
                arguments = listOf(
                    navArgument("roomId") {
                        type = NavType.StringType
                        defaultValue = "0"
                    }
                )
            ) {
                GameScreenContainer(navController)
            }

            composable(route = AppRoutes.RoomScreen.route) {
                RoomScreenContainer(navController)
            }

            composable(route = AppRoutes.RankingScreen.route) {
                RankingScreenContainer(navController)
            }
        }
    }

}

fun NavController.handleUiEvents(
    scope: CoroutineScope,
    uiEventFlow: Flow<UiEvent>,
    onUnknownEvent: ((UiEvent) -> Unit)? = null
) {
    scope.launch {
        uiEventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    SnackbarManager.showMessage(
                        message = event.snackbarEvent.message,
                        type = event.snackbarEvent.type
                    )
                }

                UiEvent.ToBack -> popBackStack()
                UiEvent.ToHome -> navigate(AppRoutes.HomeScreen.route)
                UiEvent.ToSignUp -> navigate(AppRoutes.SignUpScreen.route)
                UiEvent.ToLogin -> navigate(AppRoutes.LoginScreen.route)
                UiEvent.ToProfile -> navigate(AppRoutes.ProfileScreen.route)
                is UiEvent.ToSingleGame -> navigate(AppRoutes.SingleGameScreen.withParam("level", event.level.name))
                is UiEvent.ToGame -> navigate(AppRoutes.GameScreen.withParam("roomId", event.roomId))
                UiEvent.ToRanking -> navigate(AppRoutes.RankingScreen.route)
                UiEvent.ToRoom -> navigate(AppRoutes.RoomScreen.route)
                is UiEvent.ToRoute -> navigate(event.route)
                else -> onUnknownEvent?.invoke(event)
            }
        }
    }
}