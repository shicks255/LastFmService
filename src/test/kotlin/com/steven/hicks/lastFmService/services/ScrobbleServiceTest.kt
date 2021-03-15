package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
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

    @Test
    fun `should get most recent scrobble`() {
        `when`(scrobbleRepository.findTopByOrderByTimeDesc())
            .thenReturn(Scrobble(
                id = 1,
                name = "Test",
                artistMbid = "",
                artistName = "",
                albumName = "",
                albumMbid = "",
                time = 12345678
            ))

        val mostRecent = sut.getMostRecentScrobble()

        verify(scrobbleRepository, times(1)).findTopByOrderByTimeDesc()
        assertThat(mostRecent.time).isEqualTo(12345678)
    }

//    @Test
    fun `should get tracks`() {
        `when`(scrobbleRepository.getScrobbles(ScrobbleRequest(
            artistName = "",
            albumName = "",
            from = null,
            to = null,
            limit = null,
            sort = null
        )))
            .thenReturn(listOf(
                Scrobble(
                    id = 1,
                    name = "Test",
                    artistMbid = "",
                    artistName = "",
                    albumName = "",
                    albumMbid = "",
                    time = 12345678
                )
            ))

        val results = sut.getTracks(ScrobbleRequest(
            artistName = "",
            albumName = "",
            from = null,
            to = null,
            limit = null,
            sort = null
        ))

        verify(scrobbleRepository.getScrobbles(ScrobbleRequest(
            artistName = "",
            albumName = "",
            from = null,
            to = null,
            limit = null,
            sort = null
        )), times(1))



    }

    @Test
    fun `should get tracks grouped`() {

    }

    @Test
    fun `should get artist tracks grouped`() {

    }

    @Test
    fun `should get album tracks grouped`() {

    }

    @Test
    fun `should get artists`() {
        `when`(scrobbleRepository.suggestArtists("Pink Floyd"))
            .thenReturn(listOf("Pink Floyd"))

        val results = sut.getArtists("Pink Floyd")

        verify(scrobbleRepository, times(1)).suggestArtists("Pink Floyd")
        assertThat(results.size).isEqualTo(1)
        assertThat(results.first()).isEqualTo("Pink Floyd")
    }

    @Test
    fun `should get albums`() {
        `when`(scrobbleRepository.suggestAlbums("Dark Side"))
            .thenReturn(listOf("The Dark Side of the Moon", "Dark Side of the Moon"))

        val results = sut.getAlbums("Dark Side")

        verify(scrobbleRepository, times(1)).suggestAlbums("Dark Side")
        assertThat(results.size).isEqualTo(2)
        assertThat(results.first()).isEqualTo("The Dark Side of the Moon")
    }

}