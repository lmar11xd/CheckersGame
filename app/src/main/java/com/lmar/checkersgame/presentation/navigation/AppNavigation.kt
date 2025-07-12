package com.lmar.checkersgame.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.presentation.common.ui.auth.LoginScreen
import com.lmar.checkersgame.presentation.common.ui.auth.ProfileScreen
import com.lmar.checkersgame.presentation.common.ui.auth.SignUpScreen
import com.lmar.checkersgame.presentation.ui.GameScreen
import com.lmar.checkersgame.presentation.ui.HomeScreen
import com.lmar.checkersgame.presentation.ui.RankingScreen
import com.lmar.checkersgame.presentation.ui.RoomScreen
import com.lmar.checkersgame.presentation.ui.SingleGameScreen
import com.lmar.checkersgame.presentation.viewmodel.GameViewModel
import com.lmar.checkersgame.presentation.viewmodel.RankingViewModel
import com.lmar.checkersgame.presentation.viewmodel.SingleGameViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = AppRoutes.HomeScreen.route) {
        composable(route = AppRoutes.LoginScreen.route) {
            LoginScreen(navController)
        }

        composable(route = AppRoutes.SignUpScreen.route) {
            SignUpScreen(navController)
        }

        composable(route = AppRoutes.ProfileScreen.route) {
            ProfileScreen(navController)
        }

        composable(AppRoutes.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable(
            AppRoutes.SingleGameScreen.route + "?level={level}",
            arguments = listOf(
                navArgument("level") {
                    type = NavType.StringType
                    defaultValue = Difficulty.EASY.name
                }
            )
        ) {
            val viewModel: SingleGameViewModel = hiltViewModel()
            val gameState by viewModel.gameState.collectAsState()
            val gameTime by viewModel.gameTime.collectAsState()
            val selectedCell by viewModel.selectedCell.collectAsState()
            val userId = viewModel.userId
            val gameLevel = viewModel.gameLevel

            SingleGameScreen (
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

        composable(
            AppRoutes.GameScreen.route  + "?roomId={roomId}",
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    defaultValue = "0"
                }
            )
        ) {
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

        composable(route = AppRoutes.RoomScreen.route) {
            RoomScreen(navController)
        }

        composable(route = AppRoutes.RankingScreen.route) {
            val viewModel: RankingViewModel = hiltViewModel()
            RankingScreen(viewModel)
        }
    }
}