package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
@Suppress("ReturnCount")
class LastFmLoadingService(
    val client: LastFmRestClient,
    val scrobbleRepository: ScrobbleRepository,
    val dataLoadService: DataLoadService
) {

    companion object {
        const val SLEEP_TIME = 2_000L
        const val SAVING_TRACKS_ERROR_CODE = 5001
        const val RELOAD_THRESHOLD = 4
        const val STUCK_TIMEOUT = 15
    }

    val logger: Logger = LoggerFactory.getLogger(LastFmLoadingService::class.java)

    @Logged
    fun loadTracks(userName: String, from: Long?, pn: Int, loadEvent: DataLoad): Int {
        var tracksLoaded = loadEvent.count
        var pageNumber = pn

        try {
            while (pageNumber > 0) {
                val recentTrax = client.getRecentTracks(
                    page = pageNumber,
                    from = from,
                    userName = userName
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
//            dataLoadService.endDataLoadStatus(userName)
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
    fun loadRecent(userName: String): Int {

        // restart failed data load
        val x = dataLoadService.getRunningOrErrorDataLoad(userName)
        if (x != null) {
            if (x.status == DataLoadStatus.ERROR) {
                val loadStatus = dataLoadService.getDataLoadStatus(userName)
                dataLoadService.saveDataLoad(x.copy(status = DataLoadStatus.RUNNING))
                return loadTracks(userName, null, loadStatus.totalPages - loadStatus.currentPage, x)
            }
            // if its already running
            if (x.status === DataLoadStatus.RUNNING) {
                val loadStatus = dataLoadService.getDataLoadStatus(userName)
                if (loadStatus.timestamp.isBefore(OffsetDateTime.now().minusMinutes(STUCK_TIMEOUT.toLong()))) {
                    return loadTracks(userName, null, loadStatus.totalPages - loadStatus.currentPage, x)
                }
                return 0
            }
        }

        // if its been ran in the last 3 hours
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val lastDataLoad = dataLoadService.getMostRecentDataLoad(userName.toLowerCase())
        if (lastDataLoad != null && Duration.between(lastDataLoad.timestamp, now).toHours() < RELOAD_THRESHOLD) {
            return 0
        }

        var from: Long? = null
        if (scrobbleRepository.existsScrobbleByUserNameEquals(userName.toLowerCase())) {
            val mostRecent = scrobbleRepository.findTopByUserNameOrderByTimeDesc(userName.toLowerCase())
            from = mostRecent.time + 1
        }

        val recent = client.getRecentTracks(
            from = from,
            userName = userName.toLowerCase()
        )

        if (recent.recenttracks.track.isEmpty()) {
            return 0
        }

        val loadEvent = dataLoadService.createDataLoad(userName.toLowerCase())
        val pageNumber = recent.recenttracks.attr.totalPages
        dataLoadService.startDataLoadTracking(userName.toLowerCase(), pageNumber)
        return loadTracks(userName, from, pageNumber, loadEvent)
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
