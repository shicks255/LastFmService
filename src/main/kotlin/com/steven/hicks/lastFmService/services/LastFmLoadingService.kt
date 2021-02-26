package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

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

    fun loadDay(day: LocalDate): Int {
        try {
            val from = day.atStartOfDay(ZoneId.of("UTC")).toEpochSecond()
            val to = from + 86399

            val recent = client.getRecentTracks(
                    from = from,
                    to = to
            )

            var pageNumber = recent.recenttracks.attr.totalPages
            while (pageNumber > 0) {
                val recent = client.getRecentTracks(
                        page = pageNumber,
                        from = from,
                        to = to
                )
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
        } catch (e: Exception) {
            throw LastFmException("Problem calling last.fm", e)
        }
    }
}