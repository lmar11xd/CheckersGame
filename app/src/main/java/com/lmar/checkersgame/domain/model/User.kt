package com.lmar.checkersgame.domain.model

data class User (
    var id: String,
    var names: String = "",
    var email: String = "",
    var imageUrl: String = "",
    var createdAt: Long? = null,
    var updatedAt: Long? = null
) {
    constructor(): this("")
}

fun User.getFirstName(): String = this.names.trim().split(" ").firstOrNull() ?: ""