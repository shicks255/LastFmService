package com.steven.hicks.lastFmService.controllers.dtos

enum class SortBy(val field: String) {
    ARTIST("artist_name"),
    ALBUM("album_name"),
    TIME("time");

    fun stringify(): String {
        return field
    }
}

enum class GroupBy(val field: String) {
    ARTIST("artist_name"),
    ALBUM("album_name");

    fun stringify(): String {
        return field
    }
}

enum class TimeGroup {
    DAY,
    WEEK,
    MONTH,
    YEAR
}
