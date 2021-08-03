package com.steven.hicks.lastFmService.controllers.dtos.response

data class GroupedResponseByAlbum(
    val data: List<ResponseByAlbum>
)

data class ResponseByAlbum(
    val artistName: String,
    val albumName: String,
    var total: Int,
    val data: MutableList<DataByDay>,
)
