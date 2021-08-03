package com.steven.hicks.lastFmService

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import java.time.LocalDate

val mockScrobbleRequest = ScrobbleRequest(
    userName = "shicks255",
    artistName = "",
    albumName = "",
    from = null,
    to = null,
    limit = null,
    sort = null,
    direction = null
)

val mockGroupedScrobbleRequest = GroupedScrobbleRequest(
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
    limit = null,
    empties = false
)

val oldestNewestArtist = listOf(
    arrayOf(
        "Pink Floyd",
        1618888555.0,
        1611118094.0,
        "",
    )
)

val oldestNewestAlbum = listOf(
    arrayOf(
        "Pink Floyd",
        1618888555.0,
        1611118094.0,
        "",
        "Animals"
    )
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
        "Pink Floyd",
        1618888555.0,
        1611118094.0,
        "",
        "Animals"
    )
)