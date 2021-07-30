package com.steven.hicks.lastFmService.controllers.dtos

enum class GroupBy(val field: String) {
    ARTIST("artist_name"),
    ALBUM("album_name");

    override fun toString(): String {
        return this.name
    }
}

enum class TimeGroup {
    DAY,
    WEEK,
    MONTH,
    YEAR
}
