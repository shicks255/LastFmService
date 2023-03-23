package com.steven.hicks.lastFmService.controllers.dtos.response

data class GroupedResponseByAlbum(
    val data: List<ResponseByAlbum>
)

data class ResponseByAlbum(
    val artistName: String,
    val albumName: String,
    override var total: Int,
    override val data: MutableList<DataByDay>,
) : GroupedResponse

interface GroupedResponse {
    val total: Int
    val data: MutableList<DataByDay>
}
