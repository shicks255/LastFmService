package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.*
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigInteger

@Service
class ScrobbleService(val scrobbleRepository: ScrobbleRepository) {

    val logger = LoggerFactory.getLogger(ScrobbleService::class.java)

    fun getTracks(request: ScrobbleRequest): List<Scrobble> {
        return scrobbleRepository.getScrobbles(request)
    }

    fun getTracksGrouped(request: GroupedScrobbleRequest): List<DataByDay> {
        val data = scrobbleRepository.getGroupedScrobbles(request)

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
            val stuff = scrobbleRepository.getArtistGroupedScrobbles(request.copy(artistNames = listOf(it)))
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
            val stuff = scrobbleRepository.getAlbumGroupedScrobbles(request.copy(albumNames = listOf(it)))
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
