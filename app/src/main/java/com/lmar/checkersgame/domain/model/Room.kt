package com.lmar.checkersgame.domain.model

import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.core.utils.generateUniqueCode

data class Room(
    var roomId: String,
    var roomCode: String,
    var roomStatus: RoomStatusEnum = RoomStatusEnum.OPENED,
    var createdAt: Long? = null,
    var updatedAt: Long? = null
) {
    constructor() : this("")

    constructor(roomId: String) : this (
        roomId = roomId,
        roomCode = generateUniqueCode(),
        roomStatus = RoomStatusEnum.OPENED
    )
}