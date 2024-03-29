package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.services.ScrobbleService
import io.micrometer.core.annotation.Timed
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/search")
class TypeAheadController(
    val scrobbleService: ScrobbleService
) {

    @GetMapping("/artists")
    @Logged
    @Timed
    fun getArtists(
        @RequestParam userName: String,
        @RequestParam query: String
    ): List<String> {
        return scrobbleService.getArtists(userName, query)
    }

    @GetMapping("/albums")
    @Logged
    @Timed
    fun getAlbums(
        @RequestParam userName: String,
        @RequestParam query: String
    ): List<String> {
        return scrobbleService.getAlbums(userName, query)
    }
}
