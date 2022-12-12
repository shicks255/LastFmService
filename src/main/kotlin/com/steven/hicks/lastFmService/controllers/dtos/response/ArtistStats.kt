package com.steven.hicks.lastFmService.controllers.dtos.response

data class ArtistStats(
    val rank: Int,
    val nextArtist: String?,
    val previousArtist: String?,
    val firstPlay: Any?,
    val mostRecent: Any?,
    val topFive: List<*>,
    val plays: Int
)
