package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRunningTotalRequest
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
    fun getOldestAndNewestPlay(userName: String, type: String): Array<*>
    fun getLongestDormancy(userName: String, type: String): List<*>
    fun getScrobbleRunningTotals(request: ScrobbleRunningTotalRequest): List<*>
    fun getArtistRank(userName: String, artistName: String): List<*>
    fun getMostRecent(userName: String, artistName: String): List<*>
    fun getFirstPlay(userName: String, artistName: String): List<*>
    fun getTopFivePlays(userName: String, artistName: String): List<*>
    fun getArtistScrobbleCount(userName: String, artistName: String): Int
}
