package com.lmar.checkersgame.presentation.navigation

sealed class AppRoutes(val route: String) {
    data object LoginScreen: AppRoutes("login_screen")
    data object SignUpScreen: AppRoutes("signup_screen")
    data object HomeScreen: AppRoutes("home_screen")
    data object SingleGameScreen: AppRoutes("singlegame_screen")
    data object GameScreen: AppRoutes("game_screen")
    data object RoomScreen: AppRoutes("room_screen")
    data object ProfileScreen: AppRoutes("profile_screen")
}