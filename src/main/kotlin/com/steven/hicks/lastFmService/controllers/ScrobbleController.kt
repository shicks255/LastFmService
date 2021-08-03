package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.DataByDay
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByAlbum
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByArtist
import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.queryBuilding.Direction
import com.steven.hicks.lastFmService.services.ScrobbleService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/scrobbles")
class ScrobbleController(
    val scrobbleService: ScrobbleService,
) {

    @GetMapping()
    @Logged
    fun getScrobbles(
        @RequestParam userName: String,
        @RequestParam artistName: String?,
        @RequestParam albumName: String?,
        @RequestParam from: String?,
        @RequestParam to: String?,
        @RequestParam limit: Int?,
        @RequestParam sort: ScrobbleField?,
        @RequestParam direction: Direction?
    ): List<Scrobble> {
        val request = ScrobbleRequest(
            userName = userName,
            artistName = artistName,
            albumName = albumName,
            from = if (from != null) LocalDate.parse(from) else null,
            to = if (to != null) LocalDate.parse(to) else null,
            limit = limit,
            sort = sort,
            direction
        )
        return scrobbleService.getTracks(request)
    }

    @GetMapping("/grouped")
    @CrossOrigin("http://localhost:3000")
    @Logged
    fun getScrobblesGrouped(
        @RequestParam userName: String,
        @RequestParam from: String?,
        @RequestParam to: String?,
        @RequestParam timeGroup: TimeGroup
    ): List<DataByDay> {
        val request = GroupedScrobbleRequest(
            userName = userName,
            from = if (from != null) LocalDate.parse(from) else null,
            to = if (to != null) LocalDate.parse(to) else null,
            timeGroup = timeGroup
        )

        return scrobbleService.getTracksGrouped(request)
    }

    @GetMapping("/artistsGrouped")
    @CrossOrigin("http://localhost:3000")
    @Logged
    fun getArtistScrobblesGrouped(
        @RequestParam userName: String,
        @RequestParam from: String?,
        @RequestParam to: String?,
        @RequestParam artistNames: List<String>?,
        @RequestParam timeGroup: TimeGroup,
        @RequestParam limit: Int?,
        @RequestParam empties: Boolean? = false
    ): GroupedResponseByArtist {
        val request = GroupedArtistScrobbleRequest(
            userName = userName,
            from = LocalDate.parse(from),
            to = if (to != null) LocalDate.parse(to) else LocalDate.now(),
            artistNames = artistNames,
            timeGroup = timeGroup,
            limit = limit,
            empties = empties
        )

        return scrobbleService.getArtistTracksGrouped(request)
    }

    @GetMapping("/albumsGrouped")
    @CrossOrigin("http://localhost:3000")
    @Logged
    fun getAlbumScrobblesGrouped(
        @RequestParam userName: String,
        @RequestParam from: String?,
        @RequestParam to: String?,
        @RequestParam albumNames: List<String>?,
        @RequestParam timeGroup: TimeGroup,
        @RequestParam limit: Int?,
        @RequestParam empties: Boolean? = false
    ): GroupedResponseByAlbum {
        val request = GroupedAlbumScrobbleRequest(
            userName = userName,
            from = LocalDate.parse(from),
            to = if (to != null) LocalDate.parse(to) else null,
            albumNames = albumNames,
            timeGroup = timeGroup,
            limit = limit,
            empties = empties
        )
        return scrobbleService.getAlbumTracksGrouped(request)
    }
}
