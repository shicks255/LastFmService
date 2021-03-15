package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.*
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.dto.Track
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class ScrobbleService(val scrobbleRepository: ScrobbleRepository) {

    val logger = LoggerFactory.getLogger(ScrobbleService::class.java)

    fun getMostRecentScrobble(): Scrobble {
        return scrobbleRepository.findTopByOrderByTimeDesc()
    }

    fun saveRecentTrack(track: Track) {
        val scrobble = Scrobble(
            id = 0,
            name = track.name,
            artistMbid = track.artist.mbid,
            albumMbid = track.album.mbid,
            albumName = track.album.text,
            artistName = track.artist.text,
            time = track.date.uts
        )

        logger.info("Saving $scrobble")
        scrobbleRepository.save(scrobble)
    }

    fun getTracks(request: ScrobbleRequest): List<Scrobble> {
        return scrobbleRepository.getScrobbles(request)
    }

    fun getTracksGrouped(request: GroupedScrobbleRequest): List<DataByDay> {
        val data = scrobbleRepository.getScrobbles(request)

        return data.map {
            val o = it as Array<Any>
            val x = o.get(0) as BigInteger
            val y = o.get(1) as String
            DataByDay(x.toInt(), y)
        }
    }

    fun getArtistTracksGrouped(request: GroupedArtistScrobbleRequest): GroupedResponseByArtist {
        val data = mutableListOf<ResponseByArtist>()

        request.artistNames?.forEach {
            var counter = 0
            val stuff = scrobbleRepository.getScrobbles(request.copy(artistNames = listOf(it)))
            val dataa = stuff?.map {
                val o = it as Array<Any>
                val x = o.get(0) as BigInteger
                val y = o.get(1) as String
                counter += x.toInt()
                DataByDay(x.toInt(), y)
            }
            val grouping = ResponseByArtist(
                artistName = it,
                data = dataa,
                total = counter
            )
            data.add(grouping)
        }

        return GroupedResponseByArtist(data)
    }

    fun getAlbumTracksGrouped(request: GroupedAlbumScrobbleRequest): GroupedResponseByAlbum {
        val data = mutableListOf<ResponseByAlbum>()

        request.albumNames?.forEach {
            var counter = 0
            val stuff = scrobbleRepository.getScrobbles(request.copy(albumNames = listOf(it)))
            val dataa = stuff?.map {
                val o = it as Array<Any>
                val x = o.get(0) as BigInteger
                val y = o.get(1) as String
                counter += x.toInt()
                DataByDay(x.toInt(), y)
            }
            val grouping = ResponseByAlbum(
                albumName = it,
                data = dataa,
                total = counter
            )
            data.add(grouping)
        }

        return GroupedResponseByAlbum(data)
    }

    fun getArtists(typed: String): List<String> {
        return scrobbleRepository.suggestArtists(typed)
    }

    fun getAlbums(typed: String): List<String> {
        return scrobbleRepository.suggestAlbums(typed)
    }
}
