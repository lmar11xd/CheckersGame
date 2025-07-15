package com.lmar.checkersgame.core.utils

import androidx.compose.ui.unit.dp

object Constants {
    const val DATABASE_REFERENCE = "games/checkers"

    const val USERS_REFERENCE = "users"

    const val GAMES_REFERENCE = "games"

    const val ROOMS_REFERENCE = "rooms"

    const val STORAGE_REFERENCE = "users/img"

    const val POINTS_CAPTURE_PIECE = 1
    const val POINTS_CROWNING = 3
    const val POINTS_MATCH_DRAW = 5
    const val POINTS_MATCH_WIN = 10
    const val POINTS_ABORTED_MATCH = -5

    val PHOTO_SIZE = 150.dp

    const val ERROR_MESSAGE_AUTH = "The supplied auth credential is incorrect, malformed or has expired."
    const val ERROR_MESSAGE_ACCOUNT_EXISTS = "The email address is already in use by another account."

}