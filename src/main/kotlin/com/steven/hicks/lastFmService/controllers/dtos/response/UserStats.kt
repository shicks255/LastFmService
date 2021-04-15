package com.steven.hicks.lastFmService.controllers.dtos.response


data class UserStats(
    val oldestAndNewest: ArtistWithOldestAndNewest,
    val longestDormancy: ArtistWithLongestDormancy,
)