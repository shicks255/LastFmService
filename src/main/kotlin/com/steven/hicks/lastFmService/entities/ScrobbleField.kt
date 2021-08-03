package com.steven.hicks.lastFmService.entities

enum class ScrobbleField(val field: String) {

    COUNT_STAR("count(*)") {
        override fun toString(): String = field
    },

    ID("scrobble.id"),
    ALBUM_MBID("scrobble.album_mbid"),
    ALBUM_NAME("scrobble.album_name"),
    ARTIST_MBID("scrobble.artist_mbid"),
    ARTIST_NAME("scrobble.artist_name"),
    NAME("scrobble.name"),
    TIME("scrobble.time"),
    USER_NAME("scrobble.user_name"),

    TIMEGROUP_WEEK("to_char(to_timestamp(time), 'IYYY-IW')") {
        override fun toString(): String = field
    },
    TIMEGROUP_MONTH("to_char(to_timestamp(time), 'YYYY-MM')") {
        override fun toString(): String = field
    },
    TIMEGROUP_YEAR("to_char(to_timestamp(time), 'YYYY')") {
        override fun toString(): String = field
    },
    TIMEGROUP_DAY("to_char(to_timestamp(time), 'YYYY-MM-DD')") {
        override fun toString(): String = field
    },
}
