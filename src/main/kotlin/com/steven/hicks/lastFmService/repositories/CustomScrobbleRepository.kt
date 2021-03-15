package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.entities.data.Scrobble

interface CustomScrobbleRepository {

    fun getScrobbles(request: ScrobbleRequest): List<Scrobble>
    fun getScrobbles(request: GroupedArtistScrobbleRequest): List<Any>
    fun getScrobbles(request: GroupedAlbumScrobbleRequest): List<Any>
    fun getScrobbles(request: GroupedScrobbleRequest): List<Any>
    fun suggestArtists(typed: String): List<String>
    fun suggestAlbums(typed: String): List<String>
}