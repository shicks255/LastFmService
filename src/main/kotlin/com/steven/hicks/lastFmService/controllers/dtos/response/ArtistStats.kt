package com.steven.hicks.lastFmService.controllers.dtos.response

data class ArtistStats(
    val rank: Int,
    val firstPlay: Any?,
    val mostRecent: Any?,
    val topFive: List<*>
)
