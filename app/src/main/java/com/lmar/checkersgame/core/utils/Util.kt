package com.lmar.checkersgame.core.utils

import kotlin.random.Random

fun generateUniqueCode(): String {
    val timestamp = System.currentTimeMillis().toString().takeLast(3) // Últimos 3 dígitos del tiempo
    val random = Random.nextInt(100, 999).toString() // Número aleatorio de 3 dígitos
    return timestamp + random
}