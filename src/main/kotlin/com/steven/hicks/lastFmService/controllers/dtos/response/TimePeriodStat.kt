package com.steven.hicks.lastFmService.controllers.dtos.response

data class TimePeriodStat(
    val name: String,
    val extra: String?,
    val timeStat: TimeStat
)
