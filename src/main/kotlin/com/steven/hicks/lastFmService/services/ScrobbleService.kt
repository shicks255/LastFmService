package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.*
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
            val o = it as Array<Any>
            val plays = o.get(0) as BigInteger
            val timeGroup = o.get(1) as String
            DataByDay(plays.toInt(), timeGroup)
        }
    }

    @Logged
    fun getArtistTracksGrouped(request: GroupedArtistScrobbleRequest): GroupedResponseByArtist {
        val map = mutableMapOf<String, ResponseByArtist>()

        var start = request.from
        val dates = mutableListOf(request.from)
        while (start.isBefore(request.to)) {
            start = when (request.timeGroup) {
                TimeGroup.DAY -> start.plusDays(1)
                TimeGroup.WEEK -> start.plusWeeks(1)
                TimeGroup.MONTH -> start.plusMonths(1)
                TimeGroup.YEAR -> start.plusYears(1)
            }
            dates.add(start)
        }

        val formatter = when (request.timeGroup) {
            TimeGroup.DAY -> DateTimeFormatter.ofPattern("YYYY-MM-dd")
            TimeGroup.WEEK -> DateTimeFormatter.ofPattern("YYYY-ww")
            TimeGroup.MONTH -> DateTimeFormatter.ofPattern("YYYY-MM")
            TimeGroup.YEAR -> DateTimeFormatter.ofPattern("YYYY")
        }

        val allTimeGroups = dates.map {
            it.format(formatter)
        }

        val stuff = scrobbleRepository.getArtistGroupedScrobbles(request)
        stuff.map {
            val o = it as Array<Any>
            val count = o.get(0) as BigInteger
            val timeGroup = o.get(1) as String
            val artist = o.get(2) as String
            val dbd = DataByDay(count.toInt(), timeGroup)
            if (map.containsKey(artist)) {
                map.get(artist)!!.data.add(dbd)
                map.get(artist)!!.total += count.toInt()
            } else {
                map.putIfAbsent(
                    artist, ResponseByArtist(
                        artistName = artist,
                        data = mutableListOf(dbd),
                        total = count.toInt()
                    )
                )
            }
        }

        var listt = map.values
            .sortedByDescending { it.total }
            .toList()

        if (request.empties == true) {
            val emptyRecords = allTimeGroups.map {
                DataByDay(0, it)
            }

            listt = listt.map {
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

        return GroupedResponseByArtist(listt)
    }

    @Logged
    fun getAlbumTracksGrouped(request: GroupedAlbumScrobbleRequest): GroupedResponseByAlbum {
        val map = mutableMapOf<String, ResponseByAlbum>()

        var start = request.from
        val dates = mutableListOf<LocalDate>(request.from)
        while (start.isBefore(request.to!!)) {
            when (request.timeGroup) {
                TimeGroup.DAY -> start = start.plusDays(1)
                TimeGroup.WEEK -> start = start.plusWeeks(1)
                TimeGroup.MONTH -> start = start.plusMonths(1)
                TimeGroup.YEAR -> start = start.plusYears(1)
            }
            dates.add(start)
        }

        val formatter = when (request.timeGroup) {
            TimeGroup.DAY -> DateTimeFormatter.ofPattern("YYYY-MM-dd")
            TimeGroup.WEEK -> DateTimeFormatter.ofPattern("YYYY-ww")
            TimeGroup.MONTH -> DateTimeFormatter.ofPattern("YYYY-MM")
            TimeGroup.YEAR -> DateTimeFormatter.ofPattern("YYYY")
        }

        val allTimeGroups = dates.map {
            it.format(formatter)
        }

        val albumData = scrobbleRepository.getAlbumGroupedScrobbles(request)
        albumData.map {
            val o = it as Array<Any>
            val count = o.get(0) as BigInteger
            val timeGroup = o.get(1) as String
            val album = o.get(2) as String
            val artist = o.get(3) as String

            val dbd = DataByDay(count.toInt(), timeGroup)
            val key = album + " - " + artist
            if (map.containsKey(key)) {
                map.get(key)!!.data.add(dbd)
                map.get(key)!!.total += count.toInt()
            } else {
                map.putIfAbsent(
                    key, ResponseByAlbum(
                        artistName = artist,
                        albumName = "$album - $artist",
                        data = mutableListOf(dbd),
                        total = count.toInt()
                    )
                )
            }
        }

        var list = map.values
            .sortedByDescending { it.total }
            .filter { !it.albumName.isNullOrEmpty() }
            .toList()

        if (request.empties == true) {
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
        return scrobbleRepository.suggestArtists(userName, typed)
    }

    @Logged
    fun getAlbums(userName: String, typed: String): List<String> {
        return scrobbleRepository.suggestAlbums(userName, typed)
    }
}
