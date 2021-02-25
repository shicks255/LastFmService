package com.steven.hicks.LastFmService.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class LastFmLoadingService(
        val client: LastFmRestClient,
        val scrobbleService: ScrobbleService
) {

    fun loadAll() {
        var pageNumber = 92;
        while (pageNumber > 0) {
            val recent = client.getRecentTracks(page = pageNumber)
            val tracks = recent.recenttracks.track ?: emptyList()
            tracks.reversed().forEach {
                println("Saving ${it.name}")
                scrobbleService.saveRecentTrack(it)
            }

            println("Finished loading $pageNumber")
            pageNumber -= 1

            Thread.sleep(6000)
        }
    }

    @Transactional
    fun loadDay(day: LocalDate): Int {

        val from = day.atStartOfDay(ZoneId.of("UTC")).toEpochSecond()
        val to = from + 86399

        val recent = client.getRecentTracks(
                from = from,
                to = to
        )

        var pageNumber = recent.recenttracks.attr.totalPages

        while (pageNumber > 0) {
            val recent = client.getRecentTracks(page = pageNumber)
            val tracks = recent.recenttracks.track ?: emptyList()
            tracks.reversed().forEach {
                println("Saving ${it.name}")
                scrobbleService.saveRecentTrack(it)
            }

            println("Finished loading $pageNumber")
            pageNumber -= 1

            Thread.sleep(3000)
        }

        return recent.recenttracks.attr.total
    }

    fun loadDateRange(from: LocalDate, to: LocalDate) {

    }


}