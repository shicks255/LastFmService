package com.steven.hicks.lastFmService

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import java.time.LocalDate

val mockedScrobbleRequest = ScrobbleRequest(
    userName = "shicks255",
    artistName = "",
    albumName = "",
    from = null,
    to = null,
    limit = null,
    sort = null,
    direction = null
)

val mockedGroupedScrobbleRequest = GroupedScrobbleRequest(
    userName = "shicks255",
    from = null,
    to = null,
    timeGroup = TimeGroup.DAY
)

val mockedGroupedArtistScrobbleRequest = GroupedArtistScrobbleRequest(
    userName = "shicks255",
    from = LocalDate.of(2021, 10, 31),
    to = LocalDate.of(2021, 11, 1),
    timeGroup = TimeGroup.DAY,
    artistNames = listOf("Slayer"),
    limit = 10,
    empties = false
)

val mockedGroupedAlbumScrobbleRequest = GroupedAlbumScrobbleRequest(
    userName = "shicks255",
    from = LocalDate.of(2021, 10, 31),
    to = LocalDate.of(2021, 11, 1),
    timeGroup = TimeGroup.DAY,
    albumNames = listOf("Bleed American"),
    artistNames = null,
    limit = null,
    empties = false
)

val oldestNewestArtist = arrayOf(
    "Pink Floyd",
    1618888555.0,
    1611118094.0,
    "",
)

val oldestNewestAlbum = arrayOf(
    "Animals",
    1618888555.0,
    1611118094.0,
    "",
    "Pink Floyd"
)

val longestDormancyArtist = listOf(
    arrayOf(
        "Pink Floyd",
        1618888555.0,
        1611118094.0,
        "",
    )
)

val longestDormancyAlbum = listOf(
    arrayOf(
        "Animals",
        1618888555.0,
        1611118094.0,
        "",
        "Pink Floyd"
    )
)
