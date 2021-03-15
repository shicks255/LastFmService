package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LastFmLoadingService(
        val client: LastFmRestClient,
        val scrobbleService: ScrobbleService
) {

    companion object {
        const val SLEEP_TIME = 6000L
        const val SECONDS_IN_DAY = 86399
        const val START_PAGE = 1
    }

    val logger = LoggerFactory.getLogger(LastFmLoadingService::class.java)

    fun loadAll() {
        var pageNumber = START_PAGE;
        while (pageNumber > 0) {
            val recent = client.getRecentTracks(page = pageNumber)
            saveTracks(recent)
            pageNumber -= 1

            logger.info("Finished loading $pageNumber")
            Thread.sleep(SLEEP_TIME)
        }
    }

    fun loadRecent(): Int {
        val lastScrobble = scrobbleService.getMostRecentScrobble()

        val from = lastScrobble.time + 1

        val recent = client.getRecentTracks(
                from = from
        )

        var pageNumber = recent.recenttracks.attr.totalPages
        while (pageNumber > 0) {
            val recentTrax = client.getRecentTracks(
                    page = pageNumber,
                    from = from,
            )
            saveTracks(recentTrax)
            logger.info("Finished loading page $pageNumber")
            pageNumber -= 1

            Thread.sleep(SLEEP_TIME)
        }

        return recent.recenttracks.attr.total
    }

    private fun saveTracks(recentTrax: RecentTracks) {
        val tracks = recentTrax.recenttracks.track
        tracks.reversed().forEach { scrobbleService.saveRecentTrack(it) }
    }
}
