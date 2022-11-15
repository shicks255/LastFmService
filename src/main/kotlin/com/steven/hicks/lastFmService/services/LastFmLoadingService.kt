package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class LastFmLoadingService(
    val client: LastFmRestClient,
    val scrobbleRepository: ScrobbleRepository,
    val dataLoadService: DataLoadService
) {

    companion object {
        const val SLEEP_TIME = 2_000L
        const val SAVING_TRACKS_ERROR_CODE = 5001
        const val RELOAD_THRESHOLD = 4
    }

    val logger: Logger = LoggerFactory.getLogger(LastFmLoadingService::class.java)

    @Logged
    fun loadRecent(userName: String): Int {
        var from: Long? = null
        if (scrobbleRepository.existsScrobbleByUserNameEquals(userName.toLowerCase())) {
            val mostRecent = scrobbleRepository.findTopByUserNameOrderByTimeDesc(userName.toLowerCase())
            from = mostRecent.time + 1
        }
        var tracksLoaded = 0

        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val lastDataLoad = dataLoadService.getMostRecentDataLoad(userName.toLowerCase())
        if (lastDataLoad != null && Duration.between(lastDataLoad.timestamp, now).toHours() < RELOAD_THRESHOLD) {
            return tracksLoaded
        }

        val loadEvent = dataLoadService.createDataLoad(userName.toLowerCase())
        try {
            val recent = client.getRecentTracks(
                from = from,
                userName = userName.toLowerCase()
            )

            var pageNumber = recent.recenttracks.attr.totalPages
            dataLoadService.startDataLoadTracking(userName.toLowerCase(), pageNumber)
            while (pageNumber > 0) {
                val recentTrax = client.getRecentTracks(
                    page = pageNumber,
                    from = from,
                    userName = userName.toLowerCase()
                )
                tracksLoaded += recentTrax.recenttracks.track.size
                saveTracks(recentTrax, userName.toLowerCase())
                logger.info("Finished loading page $pageNumber for $userName")
                pageNumber -= 1
                dataLoadService.updateDataLoadStatus(userName.toLowerCase(), pageNumber)
                Thread.sleep(SLEEP_TIME)
            }
        } catch (e: LastFmException) {
            val finishedEvent = loadEvent.copy(
                status = DataLoadStatus.ERROR,
                count = tracksLoaded
            )
            dataLoadService.endDataLoadStatus(userName)
            dataLoadService.saveDataLoad(finishedEvent)
            throw e
        }
        dataLoadService.endDataLoadStatus(userName)
        val finishedEvent = loadEvent.copy(
            status = DataLoadStatus.SUCCESS,
            count = tracksLoaded
        )
        dataLoadService.saveDataLoad(finishedEvent)

        return tracksLoaded
    }

    @Logged
    private fun saveTracks(recentTrax: RecentTracks, userName: String) {
        val tracks = recentTrax.recenttracks.track
        tracks.filter { it.date != null }.reversed().forEach {
            val scrobble = Scrobble(
                id = 0,
                name = it.name,
                userName = userName.toLowerCase(),
                artistMbid = it.artist.mbid,
                artistName = it.artist.text,
                albumMbid = it.album.mbid,
                albumName = it.album.text,
                time = it.date!!.uts
            )

            try {
                scrobbleRepository.save(scrobble)
            } catch (e: Exception) {
                logger.error("Something went wrong, ${e.message}, ${e.stackTraceToString()}")
                throw LastFmException(SAVING_TRACKS_ERROR_CODE, "There was a problem saving track data")
            }
        }
    }
}
