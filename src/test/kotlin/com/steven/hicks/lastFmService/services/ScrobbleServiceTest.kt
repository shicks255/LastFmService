package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.controllers.dtos.SortBy
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
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
import java.math.BigInteger

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
            .thenReturn(
                Scrobble(
                    id = 1,
                    name = "Test",
                    artistMbid = "",
                    artistName = "",
                    albumName = "",
                    albumMbid = "",
                    time = 12345678
                )
            )

        val mostRecent = sut.getMostRecentScrobble()

        verify(scrobbleRepository, times(1)).findTopByOrderByTimeDesc()
        assertThat(mostRecent.time).isEqualTo(12345678)
    }

    @Test
    fun `should get tracks`() {
        `when`(
            scrobbleRepository.getScrobbles(
                ScrobbleRequest(
                    artistName = "",
                    albumName = "",
                    from = null,
                    to = null,
                    limit = null,
                    sort = null
                )
            )
        )
            .thenReturn(
                listOf(
                    Scrobble(
                        id = 1,
                        name = "Test",
                        artistMbid = "",
                        artistName = "",
                        albumName = "",
                        albumMbid = "",
                        time = 12345678
                    )
                )
            )

        val results = sut.getTracks(
            ScrobbleRequest(
                artistName = "",
                albumName = "",
                from = null,
                to = null,
                limit = null,
                sort = null
            )
        )

        verify(scrobbleRepository, times(1))
            .getScrobbles(
                ScrobbleRequest(
                    artistName = "",
                    albumName = "",
                    from = null,
                    to = null,
                    limit = null,
                    sort = null
                ))

        assertThat(results).hasSize(1)
        assertThat(results.first().name).isEqualTo("Test")
    }

    @Test
    fun `should get tracks grouped`() {
        `when`(
            scrobbleRepository.getGroupedScrobbles(
                GroupedScrobbleRequest(
                    from = null,
                    to = null,
                    timeGroup = TimeGroup.DAY
                )
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "11")
                )
            )

        val results = sut.getTracksGrouped(
            GroupedScrobbleRequest(
                from = null,
                to = null,
                timeGroup = TimeGroup.DAY
            )
        )

        verify(scrobbleRepository, times(1))
            .getGroupedScrobbles(
                GroupedScrobbleRequest(
                    from = null,
                    to = null,
                    timeGroup = TimeGroup.DAY
                ))

        assertThat(results).hasSize(1)
        assertThat(results.first().plays).isEqualTo(1)
        assertThat(results.first().timeGroup).isEqualTo("11")
    }

    @Test
    fun `should get artist tracks grouped`() {
        `when`(
            scrobbleRepository.getArtistGroupedScrobbles(
                GroupedArtistScrobbleRequest(
                    from = null,
                    to = null,
                    timeGroup = TimeGroup.DAY,
                    artistNames = listOf("Slayer"),
                    sort = null,
                    group = null
                )
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "11")
                )
            )

        val results = sut.getArtistTracksGrouped(
            GroupedArtistScrobbleRequest(
                from = null,
                to = null,
                timeGroup = TimeGroup.DAY,
                artistNames = listOf("Slayer"),
                sort = null,
                group = null
            )
        )

        verify(scrobbleRepository, times(1))
            .getArtistGroupedScrobbles(
                GroupedArtistScrobbleRequest(
                    from = null,
                    to = null,
                    timeGroup = TimeGroup.DAY,
                    artistNames = listOf("Slayer"),
                    sort = null,
                    group = null
                ))

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().artistName).isEqualTo("Slayer")
    }

    @Test
    fun `should get album tracks grouped`() {
        `when`(
            scrobbleRepository.getAlbumGroupedScrobbles(
                GroupedAlbumScrobbleRequest(
                    from = null,
                    to = null,
                    timeGroup = TimeGroup.DAY,
                    albumNames = listOf("Bleed American"),
                    sort = null,
                    group = null
                )
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "11")
                )
            )

        val results = sut.getAlbumTracksGrouped(
            GroupedAlbumScrobbleRequest(
                from = null,
                to = null,
                timeGroup = TimeGroup.DAY,
                albumNames = listOf("Bleed American"),
                sort = null,
                group = null
            )
        )

        verify(scrobbleRepository, times(1))
            .getAlbumGroupedScrobbles(
                GroupedAlbumScrobbleRequest(
                    from = null,
                    to = null,
                    timeGroup = TimeGroup.DAY,
                    albumNames = listOf("Bleed American"),
                    sort = null,
                    group = null
                )
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().albumName).isEqualTo("Bleed American")
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