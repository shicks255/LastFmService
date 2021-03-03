package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class LastFmLoadingService(
        val client: LastFmRestClient,
        val scrobbleService: ScrobbleService
) {

    companion object {
        const val SLEEP_TIME = 6000L
        const val SECONDS_IN_DAY = 86399
        const val START_PAGE = 333
    }

    fun loadAll() {
        var pageNumber = START_PAGE;
        while (pageNumber > 0) {
            val recent = client.getRecentTracks(page = pageNumber)
            saveTracks(recent)
            println("Finished loading $pageNumber")
            pageNumber -= 1

            Thread.sleep(SLEEP_TIME)
        }
    }

    fun loadDay(day: LocalDate): Int {
        val from = day.atStartOfDay(ZoneId.of("UTC")).toEpochSecond()
        val to = from + SECONDS_IN_DAY

        val recent = client.getRecentTracks(
                from = from,
                to = to
        )

        var pageNumber = recent.recenttracks.attr.totalPages
        while (pageNumber > 0) {
            val recentTrax = client.getRecentTracks(
                    page = pageNumber,
                    from = from,
                    to = to
            )
            saveTracks(recentTrax)
            println("Finished loading $pageNumber")
            pageNumber -= 1

            Thread.sleep(SLEEP_TIME)
        }


        return recent.recenttracks.attr.total
    }

    private fun saveTracks(recentTrax: RecentTracks) {
        val tracks = recentTrax.recenttracks.track ?: emptyList()
        tracks.reversed().forEach {
            println("Saving ${it.name}")
            scrobbleService.saveRecentTrack(it)
        }
    }
}
