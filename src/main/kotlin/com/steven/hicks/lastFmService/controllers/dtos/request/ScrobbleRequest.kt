package com.steven.hicks.lastFmService.controllers.dtos.request

import com.steven.hicks.lastFmService.controllers.dtos.SortBy
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class ScrobbleRequest(
    val artistName: String?,
    val albumName: String?,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val from: LocalDate?,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val to: LocalDate?,
    val limit: Int?,
    val sort: SortBy?,
)
