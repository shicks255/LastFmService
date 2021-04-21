package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.controllers.dtos.GroupBy
import com.steven.hicks.lastFmService.controllers.dtos.SortBy
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.DataByDay
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByAlbum
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByArtist
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.services.ScrobbleService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/scrobbles")
class ScrobbleController(
    val scrobbleService: ScrobbleService,
) {

    val logger = LoggerFactory.getLogger(ScrobbleController::class.java)

    @GetMapping()
    fun getScrobbles(
        @RequestParam
        artistName: String?,
        @RequestParam
        albumName: String?,
        @RequestParam
        from: String?,
        @RequestParam
        to: String?,
        @RequestParam
        limit: Int?,
        @RequestParam
        sort: SortBy?,
    ): List<Scrobble> {
        val request = ScrobbleRequest(
            artistName,
            albumName,
            if (from != null) LocalDate.parse(from) else null,
            if (to != null) LocalDate.parse(to) else null,
            limit,
            sort
        )
        return scrobbleService.getTracks(request)
    }

    @GetMapping("/grouped")
    @CrossOrigin("http://localhost:3000")
    fun getScrobblesGrouped(
        @RequestParam from: String?,
        @RequestParam to: String?,
        @RequestParam timeGroup: TimeGroup
    ): List<DataByDay> {
        val request = GroupedScrobbleRequest(
            if (from != null) LocalDate.parse(from) else null,
            if (to != null) LocalDate.parse(to) else null,
            timeGroup = timeGroup
        )

        return scrobbleService.getTracksGrouped(request)
    }

    @GetMapping("/artistsGrouped")
    fun getArtistScrobblesGrouped(
        @RequestParam from: String?,
        @RequestParam to: String?,
        @RequestParam artistNames: List<String>?,
        @RequestParam group: GroupBy?,
        @RequestParam sort: SortBy?,
        @RequestParam timeGroup: TimeGroup
    ): GroupedResponseByArtist {
        val request = GroupedArtistScrobbleRequest(
            if (from != null) LocalDate.parse(from) else null,
            if (to != null) LocalDate.parse(to) else null,
            artistNames,
            group,
            sort,
            timeGroup
        )

        return scrobbleService.getArtistTracksGrouped(request)
    }

    @GetMapping("/albumsGrouped")
    fun getAlbumScrobblesGrouped(
        @RequestParam from: String?,
        @RequestParam to: String?,
        @RequestParam albumNames: List<String>?,
        @RequestParam group: GroupBy?,
        @RequestParam sort: SortBy?,
        @RequestParam timeGroup: TimeGroup
    ): GroupedResponseByAlbum {
        val request = GroupedAlbumScrobbleRequest(
            if (from != null) LocalDate.parse(from) else null,
            if (to != null) LocalDate.parse(to) else null,
            albumNames,
            group,
            sort,
            timeGroup
        )
        return scrobbleService.getAlbumTracksGrouped(request)
    }
}
