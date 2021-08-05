package com.steven.hicks.lastFmService.entities.resultMappers

data class GroupedArtistResultMapper(
    val count: Int,
    val timeGroup: String,
    val artist: String
)
