package com.steven.hicks.LastFmService.services

import com.steven.hicks.LastFmService.entities.data.Scrobble
import com.steven.hicks.LastFmService.entities.dto.Track
import com.steven.hicks.LastFmService.repositories.ScrobbleRepository
import org.springframework.stereotype.Service

@Service
class ScrobbleService(val scrobbleRepository: ScrobbleRepository) {

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

        scrobbleRepository.save(scrobble)
    }

}