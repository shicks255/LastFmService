package com.steven.hicks.lastFmService.controllers.dtos.response

data class GroupedResponseByArtist(
    val data: List<ResponseByArtist>
)

data class ResponseByArtist(
    val artistName: String,
    var total: Int,
    val data: MutableList<DataByDay>,
)
