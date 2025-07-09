package com.lmar.checkersgame.domain.model

data class Player(
    var id: String,
    var name: String
) {
    constructor() : this("", "")
}