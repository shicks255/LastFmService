package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.controllers.dtos.*
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
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/scrobbles")
class ScrobbleController(val scrobbleService: ScrobbleService) {

    val logger = LoggerFactory.getLogger(ScrobbleController::class.java)

    @GetMapping
    fun getScrobbles(request: ScrobbleRequest): List<Scrobble> {
        return scrobbleService.getTracks(request)
    }

    @GetMapping("/grouped")
    @CrossOrigin("http://localhost:3000")
    fun getScrobbledGrouped(request: GroupedScrobbleRequest): List<DataByDay> {
        return scrobbleService.getTracksGrouped(request)
    }

    @GetMapping("/artistsGrouped")
    fun getArtistScrobblesGrouped(request: GroupedArtistScrobbleRequest): GroupedResponseByArtist {
        return scrobbleService.getArtistTracksGrouped(request)
    }

    @GetMapping("/albumsGrouped")
    fun getAlbumScrobblesGrouped(request: GroupedAlbumScrobbleRequest): GroupedResponseByAlbum {
        return scrobbleService.getAlbumTracksGrouped(request)
    }

    @GetMapping("/artists")
    fun getArtists(typed: String): List<String> {
        return scrobbleService.getArtists(typed)
    }

    @GetMapping("/albums")
    fun getAlbums(typed: String): List<String> {
        return scrobbleService.getAlbums(typed)
    }
}
