package com.steven.hicks.lastFmService.entities.dto

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class RecentTracks(val recenttracks: RecentTrack)

data class RecentTrack(
        @JsonProperty("@attr")
        val attr: Attr,
        val track: List<Track>,
)

data class Attr(
        val page: Int,
        val total: Int,
        val user: String,
        val perPage: Int,
        val totalPages: Int
)

data class Track(
        val artist: Artist,
        val album: Album,
        val image: List<Image>,
        val streamable: Int,
        val date: Datee,
        val url: String,
        val name: String,
        val mbid: String)

data class Artist(
        val mbid: String,
        @JsonProperty("#text") var text: String)

data class Album(
        val mbid: String,
        @JsonProperty("#text") var text: String)

data class Image(
        val size: String,
        @JsonProperty("#text") val text: String)

data class Datee(
        val uts: Long,
        @JsonProperty("#text") val text: String)
