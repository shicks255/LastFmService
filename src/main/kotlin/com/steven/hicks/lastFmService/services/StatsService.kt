package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.response.ArtistStats
import com.steven.hicks.lastFmService.controllers.dtos.response.TimePeriodStat
import com.steven.hicks.lastFmService.controllers.dtos.response.TimeStat
import com.steven.hicks.lastFmService.controllers.dtos.response.UserStats
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset

@Service
@Suppress("MagicNumber")
class StatsService(
    val scrobbleRepository: ScrobbleRepository
) {

    @Logged
    fun getArtistStats(userName: String, artistName: String): ArtistStats {
        val rank = scrobbleRepository.getArtistRank(userName, artistName)
        val mostRecent = scrobbleRepository.getMostRecent(userName, artistName)
        val firstPlay = scrobbleRepository.getFirstPlay(userName, artistName)
        val topFive = scrobbleRepository.getTopFivePlays(userName, artistName)
        val plays = scrobbleRepository.getArtistScrobbleCount(userName, artistName)

        val thing = rank.map { it as Array<Object> }
        val artistRank = thing.indexOfFirst { it[0].toString().equals(artistName, ignoreCase = true) }

        return ArtistStats(
            rank = thing[artistRank][1].toString().toInt(),
            nextArtist = thing[artistRank + 1][0].toString(),
            previousArtist = if (artistRank > 0) thing[artistRank - 1][0].toString() else null,
            firstPlay = listOf(
                (firstPlay[0] as Array<Object>)[5],
                (firstPlay[0] as Array<Object>)[2],
                (firstPlay[0] as Array<Object>)[6]
            ),
            topFive = topFive,
            mostRecent = listOf(
                (mostRecent[0] as Array<Object>)[5],
                (mostRecent[0] as Array<Object>)[2],
                (mostRecent[0] as Array<Object>)[6]
            ),
            plays = plays
        )
    }

    @Logged
    fun getStats(userName: String): UserStats {

        val oldestAndNewestArtist = GlobalScope.async { getOldestAndNewest(userName.toLowerCase(), "artist_name") }
        val longestDormancyArtist = GlobalScope.async { getLongestDormancy(userName.toLowerCase(), "artist_name") }
        val oldestAndNewestAlbum = GlobalScope.async { getOldestAndNewest(userName.toLowerCase(), "album_name") }
        val longestDormancyAlbum = GlobalScope.async { getLongestDormancy(userName.toLowerCase(), "album_name") }
        val firstTo100Artist = GlobalScope.async { getFirstToX(userName.toLowerCase(),  100, "artist_name") }
        val firstTo100Album = GlobalScope.async { getFirstToX(userName.toLowerCase(),  100, "album_name") }
        val firstTo1000Artist = GlobalScope.async { getFirstToX(userName.toLowerCase(),  1000, "artist_name") }
        val firstTo1000Album = GlobalScope.async { getFirstToX(userName.toLowerCase(),  1000, "album_name") }
        val firstTo100Song = GlobalScope.async { getFirstToX(userName.toLowerCase(), 100, "song_name") }
        val firstTo200Song = GlobalScope.async { getFirstToX(userName.toLowerCase(), 200, "song_name") }

        val stats: UserStats = runBlocking {
            awaitAll(
                oldestAndNewestAlbum,
                longestDormancyAlbum,
                oldestAndNewestArtist,
                longestDormancyArtist,
                firstTo100Artist,
                firstTo100Album,
                firstTo1000Artist,
                firstTo1000Album,
                firstTo100Song,
                firstTo200Song
            )
            return@runBlocking UserStats(
                oldestAndNewestArtist = oldestAndNewestArtist.await(),
                longestDormancyArtist = longestDormancyArtist.await(),
                oldestAndNewestAlbum = oldestAndNewestAlbum.await(),
                longestDormancyAlbum = longestDormancyAlbum.await(),
                firstTo100Artist = firstTo100Artist.await(),
                firstTo100Album = firstTo100Album.await(),
                firstTo1000Artist = firstTo1000Artist.await(),
                firstTo1000Album = firstTo1000Album.await(),
                firstTo100Song = firstTo100Song.await(),
                firstTo200Song = firstTo200Song.await(),
            )
        }

        return stats
    }

    private fun createStatsFromFields(result: Array<*>, field: String): TimePeriodStat {
        val name = result[0] as String
        val newest = (result[1] as Double).toLong()
        val oldest = (result[2] as Double).toLong()
        var extra: String? = null
        if (field == "album_name") {
            extra = result[4] as String
        }

        val firstDate = LocalDate.ofInstant(Instant.ofEpochSecond(oldest), ZoneOffset.UTC)
        val lastDate = LocalDate.ofInstant(Instant.ofEpochSecond(newest), ZoneOffset.UTC)
        val period = Period.between(firstDate, lastDate)

        return TimePeriodStat(
            name = name,
            extra = extra,
            timeStat = TimeStat(
                oldest = firstDate,
                newest = lastDate,
                difference = period
            )
        )
    }

    @Logged
    suspend fun getLongestDormancy(userName: String, field: String): TimePeriodStat {

        val result = scrobbleRepository.getLongestDormancy(userName.toLowerCase(), field)
        val resu = result.first() as Array<*>
        return createStatsFromFields(resu, field)
    }

    @Logged
    suspend fun getOldestAndNewest(userName: String, field: String): TimePeriodStat {

        val result = scrobbleRepository.getOldestAndNewestPlay(userName.toLowerCase(), field)
        return createStatsFromFields(result, field)
    }

    @Logged
    suspend fun getFirstToX(userName: String, threshold: Int, field: String): TimePeriodStat? {

        val result: Array<*>? = when (field) {
            "song_name" -> scrobbleRepository.getFirstToXSong(userName.toLowerCase(), threshold)
            "artist_name" -> scrobbleRepository.getFirstToXArtist(userName.toLowerCase(), threshold)
            "album_name" -> scrobbleRepository.getFirstToXAlbum(userName.toLowerCase(), threshold)
            else -> null
        }

        if (result == null) {
            return null
        }

        val name = result[0] as String
        val extra = if (field == "song_name" || field == "album_name") {
            result[1] as String
        } else null

        val time = if (field == "song_name" || field == "album_name") {
            (result[2] as Double).toLong()
        } else (result[1] as Double).toLong()

        return TimePeriodStat(
            name = name,
            extra = extra,
            timeStat = TimeStat(
                oldest = LocalDate.ofInstant(Instant.ofEpochSecond(time), ZoneOffset.UTC),
                newest = LocalDate.ofInstant(Instant.ofEpochSecond(time), ZoneOffset.UTC),
                difference = Period.of(0, 0, 0),
            )
        )
    }
}
