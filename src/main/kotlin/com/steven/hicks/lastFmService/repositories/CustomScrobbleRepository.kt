package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedAlbumResultMapper
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedArtistResultMapper
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedResultMapper

interface CustomScrobbleRepository {

    fun getScrobbles(request: ScrobbleRequest): List<Scrobble>
    fun getArtistGroupedScrobbles(request: GroupedArtistScrobbleRequest): List<GroupedArtistResultMapper>
    fun getAlbumGroupedScrobbles(request: GroupedAlbumScrobbleRequest): List<GroupedAlbumResultMapper>
    fun getGroupedScrobbles(request: GroupedScrobbleRequest): List<GroupedResultMapper>
    fun suggestArtists(userName: String, typed: String): List<String>
    fun suggestAlbums(userName: String, typed: String): List<String>
    fun getOldestAndNewestPlay(userName: String, type: String): List<Any>
    fun getLongestDormancy(userName: String, type: String): List<Any>
}
