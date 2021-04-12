package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.services.ScrobbleService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/search")
class TypeAheadController(
    val scrobbleService: ScrobbleService
) {
    val logger = LoggerFactory.getLogger(TypeAheadController::class.java)

    @GetMapping("/artists")
    fun getArtists(@RequestParam query: String): List<String> {
        return scrobbleService.getArtists(query)
    }

    @GetMapping("/albums")
    fun getAlbums(@RequestParam query: String): List<String> {
        return scrobbleService.getAlbums(query)
    }
}
