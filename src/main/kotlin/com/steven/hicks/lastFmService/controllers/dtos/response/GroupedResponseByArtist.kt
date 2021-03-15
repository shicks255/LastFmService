package com.steven.hicks.lastFmService.controllers.dtos.response

data class GroupedResponseByArtist(
    val data: List<ResponseByArtist>
)

data class ResponseByArtist(
    val artistName: String,
    val total: Int,
    val data: List<DataByDay>,
)
