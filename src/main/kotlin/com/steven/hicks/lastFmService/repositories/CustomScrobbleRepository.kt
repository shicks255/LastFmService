package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.entities.data.Scrobble

interface CustomScrobbleRepository {

    fun getScrobbles(request: ScrobbleRequest): List<Scrobble>
    fun getArtistGroupedScrobbles(request: GroupedArtistScrobbleRequest): List<Any>
    fun getAlbumGroupedScrobbles(request: GroupedAlbumScrobbleRequest): List<Any>
    fun getGroupedScrobbles(request: GroupedScrobbleRequest): List<Any>
    fun suggestArtists(userName: String, typed: String): List<String>
    fun suggestAlbums(userName: String, typed: String): List<String>
    fun getOldestAndNewestPlay(userName: String, type: String): List<Any>
    fun getLongestDormancy(userName: String, type: String): List<Any>
}
