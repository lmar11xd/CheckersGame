package com.lmar.checkersgame.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.presentation.common.components.Snackbar
import com.lmar.checkersgame.presentation.common.components.SnackbarManager
import com.lmar.checkersgame.presentation.common.components.SnackbarType
import com.lmar.checkersgame.presentation.common.ui.auth.LoginScreenContainer
import com.lmar.checkersgame.presentation.common.ui.auth.ProfileScreenContainer
import com.lmar.checkersgame.presentation.common.ui.auth.SignUpScreenContainer
import com.lmar.checkersgame.presentation.ui.GameScreen
import com.lmar.checkersgame.presentation.ui.HomeScreenContainer
import com.lmar.checkersgame.presentation.ui.RankingScreen
import com.lmar.checkersgame.presentation.ui.RoomScreen
import com.lmar.checkersgame.presentation.ui.SingleGameScreen
import com.lmar.checkersgame.presentation.viewmodel.GameViewModel
import com.lmar.checkersgame.presentation.viewmodel.RankingViewModel
import com.lmar.checkersgame.presentation.viewmodel.RoomViewModel
import com.lmar.checkersgame.presentation.viewmodel.SingleGameViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf(SnackbarType.INFO) }

    // Escuchar mensajes del manager
    LaunchedEffect(Unit) {
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

@Composable
fun SingleGameScreenContainer(navController: NavHostController) {
    val viewModel: SingleGameViewModel = hiltViewModel()
    val gameState by viewModel.gameState.collectAsState()
    val gameTime by viewModel.gameTime.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val userId = viewModel.userId
    val gameLevel = viewModel.gameLevel

    SingleGameScreen(
        gameState = gameState,
        gameTime = gameTime,
        gameLevel = gameLevel,
        selectedCell = selectedCell,
        userId = userId,
        onCellClick = { row, col -> viewModel.onCellClick(row, col) },
        onRematch = { viewModel.resetGame() },
        onAbortGame = {
            navController.popBackStack()
        },
        onLeaveRoom = {
            navController.popBackStack()
        },
        onExit = {
            navController.popBackStack()
        }
    )
}

@Composable
fun GameScreenContainer(navController: NavHostController) {
    val viewModel: GameViewModel = hiltViewModel()
    val gameState by viewModel.gameState.collectAsState()
    val roomState by viewModel.roomState.collectAsState()
    val gameTime by viewModel.gameTime.collectAsState()
    val scores by viewModel.scores.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val rematchRequested by viewModel.rematchRequested.collectAsState()
    val userId = viewModel.userId

    GameScreen(
        gameState = gameState,
        roomState = roomState,
        gameTime = gameTime,
        selectedCell = selectedCell,
        userId = userId,
        scores = scores,
        onCellClick = { row, col -> viewModel.onCellClick(row, col) },
        onRematch = { viewModel.requestRematch() },
        rematchRequested = rematchRequested,
        onAbortGame = {
            viewModel.abortGame()
            navController.popBackStack()
        },
        onLeaveRoom = {
            viewModel.leaveRoom()
            navController.popBackStack()
        },
        onExit = {
            navController.popBackStack()
        }
    )
}

@Composable
fun RoomScreenContainer(navController: NavHostController) {
    val viewModel: RoomViewModel = hiltViewModel()

    RoomScreen(
        onCreateRoom = {
            viewModel.createRoom { roomId ->
                navController.navigate(AppRoutes.GameScreen.withParam("roomId", roomId))
            }
        },
        onJoinRoom = { code ->
            viewModel.searchRoomByCode(code) { success, roomId ->
                if (success) {
                    navController.navigate(AppRoutes.GameScreen.withParam("roomId", roomId))
                }
            }
        },
        onBackAction = { navController.popBackStack() }
    )
}

@Composable
fun RankingScreenContainer(navController: NavHostController) {
    val viewModel: RankingViewModel = hiltViewModel()
    val topPlayers by viewModel.topPlayers.collectAsState()

    RankingScreen(topPlayers)
}