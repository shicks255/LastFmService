package com.steven.hicks.lastFmService.controllers.dtos.response

data class OldestAndNewestStat(
    val name: String,
    val extra: String?,
    val timeStat: TimeStat
)
