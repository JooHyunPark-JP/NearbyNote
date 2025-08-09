package com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data

data class SelectedNoteInfo(
    val id: Long,
    val content: String,
    val createdAt: Long,
    val updateAt: Long,
    val radius: Float,
    val address: String
)