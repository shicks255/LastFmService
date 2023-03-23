package com.steven.hicks.lastFmService.controllers.dtos.response

data class GroupedResponseByArtist(
    val data: List<ResponseByArtist>
)

data class ResponseByArtist(
    val artistName: String,
    override var total: Int,
    override val data: MutableList<DataByDay>,
) : GroupedResponse
