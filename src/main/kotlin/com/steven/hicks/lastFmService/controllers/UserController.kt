package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.controllers.dtos.response.LoadStatusResponse
import com.steven.hicks.lastFmService.controllers.dtos.response.UserStats
import com.steven.hicks.lastFmService.services.DataLoadService
import com.steven.hicks.lastFmService.services.LastFmLoadingService
import com.steven.hicks.lastFmService.services.StatsService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    val statsService: StatsService,
    val dataLoadService: DataLoadService,
    val lastFmLoadingService: LastFmLoadingService
) {

    @GetMapping("/stats")
    @CrossOrigin("http://localhost:3000")
    fun getUserStats(
        @RequestParam userName: String
    ): UserStats {

        return statsService.getStats(userName)
    }

    @GetMapping("/load")
    fun loadScrobbles(
        @RequestParam userName: String
    ): HttpStatus {
        CompletableFuture.supplyAsync {
            lastFmLoadingService.loadRecent(userName)
        }

        return HttpStatus.ACCEPTED
    }

    @GetMapping("/loadStatus")
    fun getLoadStatus(
        @RequestParam userName: String
    ): LoadStatusResponse {
        return dataLoadService.getDataLoadStatus(userName)
    }
}
