package com.steven.hicks.lastFmService.controllers.dtos.response

import java.time.LocalDate
import java.time.Period

data class ArtistWithOldestAndNewest(
    val artistName: String,
    val oldest: LocalDate,
    val newest: LocalDate,
    val difference: Period,
)