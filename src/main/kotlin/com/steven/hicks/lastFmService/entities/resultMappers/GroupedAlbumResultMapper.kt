package com.steven.hicks.lastFmService.entities.resultMappers

data class GroupedAlbumResultMapper(
    val count: Int,
    val timeGroup: String,
    val albumName: String,
    val artist: String
)
