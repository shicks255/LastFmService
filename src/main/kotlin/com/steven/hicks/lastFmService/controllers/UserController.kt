package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.response.LoadStatusResponse
import com.steven.hicks.lastFmService.controllers.dtos.response.UserStats
import com.steven.hicks.lastFmService.services.DataLoadService
import com.steven.hicks.lastFmService.services.LastFmLoadingService
import com.steven.hicks.lastFmService.services.StatsService
import io.micrometer.core.annotation.Timed
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    val statsService: StatsService,
    val dataLoadService: DataLoadService,
    val lastFmLoadingService: LastFmLoadingService
) {

    @GetMapping("/stats")
    @Logged
    @Timed
    fun getUserStats(
        @RequestParam userName: String
    ): UserStats {

        return statsService.getStats(userName)
    }

    @PostMapping("/load")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @Logged
    @Timed
    fun loadScrobbles(
        @RequestParam userName: String
    ): HttpStatus {
        lastFmLoadingService.loadRecent(userName)
        return HttpStatus.ACCEPTED
    }

    @GetMapping("/loadStatus")
    @Logged
    @Timed
    fun getLoadStatus(
        @RequestParam userName: String
    ): LoadStatusResponse {
        return dataLoadService.getDataLoadStatus(userName)
    }
}
