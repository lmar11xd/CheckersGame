package com.lmar.checkersgame.domain.model

import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.core.utils.generateUniqueCode

data class Room(
    val roomId: String = "",
    val roomCode: String = "",
    val roomStatus: RoomStatusEnum = RoomStatusEnum.OPENED,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
) {
    constructor() : this("")

    constructor(roomId: String) : this (
        roomId = roomId,
        roomCode = generateUniqueCode(),
        roomStatus = RoomStatusEnum.OPENED
    )
}