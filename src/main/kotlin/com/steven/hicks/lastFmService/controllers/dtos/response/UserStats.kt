package com.steven.hicks.lastFmService.controllers.dtos.response

data class UserStats(
    val oldestAndNewestArtist: TimePeriodStat,
    val longestDormancyArtist: TimePeriodStat,
    val oldestAndNewestAlbum: TimePeriodStat,
    val longestDormancyAlbum: TimePeriodStat
)
