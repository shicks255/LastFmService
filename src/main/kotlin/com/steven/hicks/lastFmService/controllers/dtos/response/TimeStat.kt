package com.steven.hicks.lastFmService.controllers.dtos.response

import java.time.LocalDate
import java.time.Period

data class TimeStat (
    val oldest: LocalDate,
    val newest: LocalDate,
    val difference: Period
)