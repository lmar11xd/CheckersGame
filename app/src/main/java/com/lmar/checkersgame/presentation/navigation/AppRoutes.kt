package com.lmar.checkersgame.presentation.navigation

sealed class AppRoutes(val route: String) {
    data object LoginScreen: AppRoutes("login_screen")
    data object SignUpScreen: AppRoutes("signup_screen")
    data object ProfileScreen: AppRoutes("profile_screen")
    data object ResetPasswordScreen: AppRoutes("resetpassword_screen")
    data object HomeScreen: AppRoutes("home_screen")
    data object SingleGameScreen: AppRoutes("singlegame_screen")
    data object GameScreen: AppRoutes("game_screen")
    data object RoomScreen: AppRoutes("room_screen")
    data object RankingScreen: AppRoutes("ranking_screen")

    fun withArgs(vararg args: String): String =
        route + args.joinToString(separator = "&", prefix = "?") { "$it={$it}" }

    fun withParam(key: String, value: String): String =
        "$route?$key=$value"
}