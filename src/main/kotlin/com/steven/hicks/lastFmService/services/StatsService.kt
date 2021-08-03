package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.response.LongestDormancyStat
import com.steven.hicks.lastFmService.controllers.dtos.response.OldestAndNewestStat
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
    fun getStats(userName: String): UserStats {

        val oldestAndNewestArtist = GlobalScope.async { getOldestAndNewest(userName, "artist_name") }
        val longestDormancyArtist = GlobalScope.async { getLongestDormancy(userName, "artist_name") }
        val oldestAndNewestAlbum = GlobalScope.async { getOldestAndNewest(userName, "album_name") }
        val longestDormancyAlbum = GlobalScope.async { getLongestDormancy(userName, "album_name") }

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

    @Logged
    suspend fun getLongestDormancy(userName: String, field: String): LongestDormancyStat {
        val result = scrobbleRepository.getLongestDormancy(userName, field)

        val resu = result.first() as Array<Object>

        val name = resu[0] as String
        val newest = (resu[1] as Double).toLong()
        val oldest = (resu[2] as Double).toLong()
        var extra: String? = null
        if (field == "album_name") {
            extra = resu[4] as String
        }

        val firstDate = LocalDate.ofInstant(Instant.ofEpochSecond(oldest), ZoneOffset.UTC)
        val lastDate = LocalDate.ofInstant(Instant.ofEpochSecond(newest), ZoneOffset.UTC)

        val period = Period.between(firstDate, lastDate)

        return LongestDormancyStat(
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
    suspend fun getOldestAndNewest(userName: String, field: String): OldestAndNewestStat {

        val result = scrobbleRepository.getOldestAndNewestPlay(userName, field)

        val resu = result.first() as Array<Object>

        val name = resu[0] as String
        val newest = (resu[1] as Double).toLong()
        val oldest = (resu[2] as Double).toLong()
        var extra: String? = null
        if (field == "album_name") {
            extra = resu[4] as String
        }

        val firstDate = LocalDate.ofInstant(Instant.ofEpochSecond(oldest), ZoneOffset.UTC)
        val lastDate = LocalDate.ofInstant(Instant.ofEpochSecond(newest), ZoneOffset.UTC)

        val period = Period.between(firstDate, lastDate)

        return OldestAndNewestStat(
            name = name,
            extra = extra,
            timeStat = TimeStat(
                oldest = firstDate,
                newest = lastDate,
                difference = period
            )
        )
    }
}