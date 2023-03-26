package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRunningTotalRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.DataByDay
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponse
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
class ScrobbleService(
    val scrobbleRepository: ScrobbleRepository
) {

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

        val artistData = scrobbleRepository.getArtistGroupedScrobbles(request)
        artistData.forEach {
            val artist = it.artist
            val count = it.count
            val dbd = DataByDay(it.count, it.timeGroup)
            artistDataMap.compute(artist) { key, value ->
                if (value == null)
                    ResponseByArtist(key, count, mutableListOf(dbd))
                else {
                    value.data.add(dbd)
                    value.total += count
                    value
                }
            }
        }

        var filteredList = artistDataMap.values
            .sortedByDescending { it.total }
            .take(request.limit ?: artistDataMap.size)
            .toList()

        if (request.empties == true) {
            val dates = generateDateList(request.from, request.to, request.timeGroup)
            val formatter = getFormatter(request.timeGroup)
            filteredList = populateEmpties(dates, formatter, filteredList) as MutableList<ResponseByArtist>
        }

        return GroupedResponseByArtist(filteredList)
    }

    @Logged
    private fun populateEmpties(
        dates: List<LocalDate>,
        formatter: DateTimeFormatter,
        list: List<GroupedResponse>
    ): List<GroupedResponse> {
        val allTimeGroups = dates.map {
            it.format(formatter)
        }
        val emptyRecords = allTimeGroups.map {
            DataByDay(0, it)
        }

        return list.map { it ->
            val data = it.data
            val timeGroupsAdded = data.map { it.timeGroup }
            val timeGroupsToAdd = emptyRecords.filter { !timeGroupsAdded.contains(it.timeGroup) }
            data.addAll(timeGroupsToAdd)
            data.sortBy { it.timeGroup }
            it
        }
    }

    @Logged
    fun getScrobbleRunningTotals(request: ScrobbleRunningTotalRequest): RunningTotalResponse {
        val data = scrobbleRepository.getScrobbleRunningTotals(request)

        val x = data.map {
            val dataArray = it as Array<Object>
            RunningTotals(dataArray[0] as String, (dataArray[1] as BigDecimal).toInt())
        }
            .toMutableList()

        val start = Year.parse(x[0].timeGroup).value - 1
        val years = x.map { it.timeGroup }

        for (i in start..Year.now().value) {
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
            val dates = generateDateList(request.from, request.to, request.timeGroup)
            val formatter = getFormatter(request.timeGroup)
            list = populateEmpties(dates, formatter, list) as MutableList<ResponseByAlbum>
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

        if (timeGroup == TimeGroup.YEAR) {
            dates.removeIf {
                it.year > LocalDate.now().year
            }
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
