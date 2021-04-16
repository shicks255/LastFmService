package com.steven.hicks.lastFmService.controllers.dtos.response


data class UserStats(
    val oldestAndNewestArtist: OldestAndNewestStat,
    val longestDormancyArtist: LongestDormancyStat,
    val oldestAndNewestAlbum: OldestAndNewestStat,
    val longestDormancyAlbum: LongestDormancyStat
)
