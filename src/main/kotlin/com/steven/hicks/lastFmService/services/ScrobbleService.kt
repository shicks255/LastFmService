package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRunningTotalRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.DataByDay
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByAlbum
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByArtist
import com.steven.hicks.lastFmService.controllers.dtos.response.ResponseByAlbum
import com.steven.hicks.lastFmService.controllers.dtos.response.ResponseByArtist
import com.steven.hicks.lastFmService.controllers.dtos.response.RunningTotalResponse
import com.steven.hicks.lastFmService.controllers.dtos.response.RunningTotals
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

@Service
@Suppress("MagicNumber")
class ScrobbleService(val scrobbleRepository: ScrobbleRepository) {

    @Logged
    fun getTracks(request: ScrobbleRequest): List<Scrobble> {
        return scrobbleRepository.getScrobbles(request)
    }

    @Logged
    fun getTracksGrouped(request: GroupedScrobbleRequest): List<DataByDay> {
        val data = scrobbleRepository.getGroupedScrobbles(request)

        return data.map {
            DataByDay(it.count, it.timeGroup)
        }
    }

    @Logged
    fun getArtistTracksGrouped(request: GroupedArtistScrobbleRequest): GroupedResponseByArtist {
        val artistDataMap = mutableMapOf<String, ResponseByArtist>()

        val dates = generateDateList(request.from, request.to, request.timeGroup)
        val formatter = getFormatter(request.timeGroup)

        val artistData = scrobbleRepository.getArtistGroupedScrobbles(request)
        artistData.forEach {
            val artist = it.artist
            val count = it.count
            val dbd = DataByDay(it.count, it.timeGroup)
            artistDataMap.compute(artist) { _, t ->
                if (t == null)
                    ResponseByArtist(artist, count, mutableListOf(dbd))
                else {
                    t.data.add(dbd)
                    t.total += count
                    t
                }
            }
        }

        var list = artistDataMap.values
            .sortedByDescending { it.total }
            .take(request.limit ?: artistDataMap.size)
            .toList()

        if (request.empties == true) {
            val allTimeGroups = dates.map {
                it.format(formatter)
            }
            val emptyRecords = allTimeGroups.map {
                DataByDay(0, it)
            }

            list = list.map { it ->
                val da = it.data
                val timeGroupsAdded = da.map { it.timeGroup }
                val timeGroupsToAdd = emptyRecords.filter { !timeGroupsAdded.contains(it.timeGroup) }
                da.addAll(timeGroupsToAdd)
                da.sortBy { it.timeGroup }
                it.copy(
                    data = da
                )
            }
        }

        return GroupedResponseByArtist(list)
    }

    @Logged
    fun getScrobbleRunningTotals(request: ScrobbleRunningTotalRequest): RunningTotalResponse {
        val data = scrobbleRepository.getScrobbleRunningTotals(request)

        println(data)

        val x = data.map {
            val thi = it as Array<java.lang.Object>
            RunningTotals(thi.get(0) as String, (thi.get(1) as BigDecimal).toInt())
        }
            .toMutableList()

        val start = Year.parse(x.get(0).timeGroup)

        val years = x.map { it.timeGroup }
        for (i in start.value - 1..Year.now().value) {
            val year = Year.of(i)
            if (!years.contains(year.toString())) {
                x.add(
                    RunningTotals(year.toString(), 0)
                )
            }
        }

        var runningTotal = 0
        x.sortBy { it.timeGroup }

        val y = x.map {
            if (it.count == 0) {
                it.copy(count = runningTotal)
            } else {
                runningTotal = it.count
                it
            }
        }

        return RunningTotalResponse(y)
    }

    @Logged
    fun getAlbumTracksGrouped(request: GroupedAlbumScrobbleRequest): GroupedResponseByAlbum {
        val albumDataMap = mutableMapOf<String, ResponseByAlbum>()

        val dates = generateDateList(request.from, request.to, request.timeGroup)
        val formatter = getFormatter(request.timeGroup)

        val albumData = scrobbleRepository.getAlbumGroupedScrobbles(request)
        albumData.forEach {
            val count = it.count
            val timeGroup = it.timeGroup
            val album = it.albumName
            val artist = it.artist

            val dbd = DataByDay(count.toInt(), timeGroup)
            val key = "$album - $artist"
            albumDataMap.compute(key) { k, v ->
                if (v == null)
                    ResponseByAlbum(artist, k, count, mutableListOf(dbd))
                else {
                    v.data.add(dbd)
                    v.total += count
                    v
                }
            }
        }

        var list = albumDataMap.values
            .sortedByDescending { it.total }
            .filter { it.albumName.isNotEmpty() }
            .take(request.limit ?: albumDataMap.size)
            .toList()

        if (request.empties == true) {

            val allTimeGroups = dates.map {
                it.format(formatter)
            }
            val emptyRecords = allTimeGroups.map {
                DataByDay(0, it)
            }

            list = list.map { it ->
                val da = it.data
                val timeGroupsAdded = da.map { it.timeGroup }
                val timeGroupedToAdd = emptyRecords.filter { !timeGroupsAdded.contains(it.timeGroup) }
                da.addAll(timeGroupedToAdd)
                da.sortBy { it.timeGroup }
                it.copy(
                    data = da
                )
            }
        }

        return GroupedResponseByAlbum(list)
    }

    @Logged
    fun getArtists(userName: String, typed: String): List<String> {
        return scrobbleRepository.suggestArtists(userName.toLowerCase(), typed)
    }

    @Logged
    fun getAlbums(userName: String, typed: String): List<String> {
        return scrobbleRepository.suggestAlbums(userName.toLowerCase(), typed)
    }

    private fun generateDateList(start: LocalDate, end: LocalDate, timeGroup: TimeGroup): List<LocalDate> {
        val dates = mutableListOf(start)
        var currentDate = start
        while (currentDate.isBefore(end)) {
            currentDate = when (timeGroup) {
                TimeGroup.DAY -> currentDate.plusDays(1)
                TimeGroup.WEEK -> currentDate.plusWeeks(1)
                TimeGroup.MONTH -> currentDate.plusMonths(1)
                TimeGroup.YEAR -> currentDate.plusYears(1)
            }
            dates.add(currentDate)
        }

        return dates
    }

    private fun getFormatter(timeGroup: TimeGroup): DateTimeFormatter {
        return when (timeGroup) {
            TimeGroup.DAY -> ofPattern("YYYY-MM-dd")
            TimeGroup.WEEK -> ofPattern("YYYY-ww")
            TimeGroup.MONTH -> ofPattern("YYYY-MM")
            TimeGroup.YEAR -> ofPattern("YYYY")
        }
    }
}
