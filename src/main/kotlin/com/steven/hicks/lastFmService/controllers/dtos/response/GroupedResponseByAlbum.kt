package com.steven.hicks.lastFmService.controllers.dtos.response

data class GroupedResponseByAlbum(
    val data: List<ResponseByAlbum>
)

data class ResponseByAlbum(
    val albumName: String,
    val total: Int,
    val data: List<DataByDay>,
)