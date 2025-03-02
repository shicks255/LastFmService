package com.steven.hicks.lastFmService.controllers.dtos.response

data class UserStats(
    val oldestAndNewestArtist: TimePeriodStat,
    val longestDormancyArtist: TimePeriodStat,
    val oldestAndNewestAlbum: TimePeriodStat,
    val longestDormancyAlbum: TimePeriodStat,
    val firstTo100Artist: TimePeriodStat?,
    val firstTo100Album: TimePeriodStat?,
    val firstTo1000Artist: TimePeriodStat?,
    val firstTo1000Album: TimePeriodStat?,
    val firstTo100Song: TimePeriodStat?,
    val firstTo200Song: TimePeriodStat?,
)
