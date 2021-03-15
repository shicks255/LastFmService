package com.steven.hicks.lastFmService.controllers.dtos.request

import com.steven.hicks.lastFmService.controllers.dtos.GroupBy
import com.steven.hicks.lastFmService.controllers.dtos.SortBy
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class GroupedArtistScrobbleRequest(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val from: LocalDate?,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val to: LocalDate?,
    val artistNames: List<String>?,
    val group: GroupBy?,
    val sort: SortBy?,
    val timeGroup: TimeGroup
)
