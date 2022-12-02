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

        return ArtistStats(
            rank = rank.first().toInt(),
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
            )
        )
    }

    @Logged
    fun getStats(userName: String): UserStats {

        val oldestAndNewestArtist = GlobalScope.async { getOldestAndNewest(userName.toLowerCase(), "artist_name") }
        val longestDormancyArtist = GlobalScope.async { getLongestDormancy(userName.toLowerCase(), "artist_name") }
        val oldestAndNewestAlbum = GlobalScope.async { getOldestAndNewest(userName.toLowerCase(), "album_name") }
        val longestDormancyAlbum = GlobalScope.async { getLongestDormancy(userName.toLowerCase(), "album_name") }

        val stats: UserStats = runBlocking {
            awaitAll(oldestAndNewestAlbum, longestDormancyAlbum, oldestAndNewestArtist, longestDormancyArtist)
            return@runBlocking UserStats(
                oldestAndNewestArtist = oldestAndNewestArtist.await(),
                longestDormancyArtist = longestDormancyArtist.await(),
                oldestAndNewestAlbum = oldestAndNewestAlbum.await(),
                longestDormancyAlbum = longestDormancyAlbum.await()
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
}
