package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.dto.Album
import com.steven.hicks.lastFmService.entities.dto.Artist
import com.steven.hicks.lastFmService.entities.dto.Datee
import com.steven.hicks.lastFmService.entities.dto.Track
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ScrobbleServiceTest {

    @Mock
    lateinit var scrobbleRepository: ScrobbleRepository

    @Captor
    lateinit var captor: ArgumentCaptor<Scrobble>

    @InjectMocks
    lateinit var sut: ScrobbleService

    @Test
    fun `should save track`() {
        `when`(scrobbleRepository.save(any()))
            .thenReturn(any())

        val track = Track(
            artist = Artist("", ""),
            Album("", ""),
            emptyList(),
            1,
            Datee(1L, ""),
            "", "", ""
        )

        sut.saveRecentTrack(track)

        verify(scrobbleRepository, times(1)).save(any())
        verify(scrobbleRepository).save(captor.capture())
        assertThat(captor.value)
            .satisfies { s ->
                s.albumMbid == track.album.mbid &&
                        s.albumName == track.album.text &&
                        s.artistMbid == track.artist.mbid &&
                        s.artistName == track.artist.text &&
                        s.name == track.name
            }
    }
}