package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.dto.Album
import com.steven.hicks.lastFmService.entities.dto.Artist
import com.steven.hicks.lastFmService.entities.dto.Attr
import com.steven.hicks.lastFmService.entities.dto.Datee
import com.steven.hicks.lastFmService.entities.dto.RecentTrack
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import com.steven.hicks.lastFmService.entities.dto.Track
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class LastFmLoadingServiceTest {

    @Mock
    lateinit var client: LastFmRestClient

    @Mock
    lateinit var scrobbleRepository: ScrobbleRepository

    @Mock
    lateinit var dataLoadService: DataLoadService

    @InjectMocks
    lateinit var sut: LastFmLoadingService

    @Test
    fun `should load recent`() {

        val mostRecent = Scrobble(
            id = 1,
            userName = "shicks255",
            name = "Test",
            artistMbid = "",
            artistName = "",
            albumName = "",
            albumMbid = "",
            time = 12345678
        )

        `when`(scrobbleRepository.existsScrobbleByUserNameEquals("shicks255"))
            .thenReturn(true)
        `when`(scrobbleRepository.findTopByUserNameOrderByTimeDesc("shicks255"))
            .thenReturn(mostRecent)
        `when`(client.getRecentTracks(from = 12345679L, to = null, page = 1, userName = "shicks255"))
            .thenReturn(createRecentTracks())
        `when`(client.getRecentTracks(from = 12345679L, to = null, page = null, userName = "shicks255"))
            .thenReturn(createRecentTracks())

        val result = sut.loadRecent("shicks255")

        assertThat(result)
            .isEqualTo(1)
        verify(scrobbleRepository, times(1))
            .existsScrobbleByUserNameEquals("shicks255")
        verify(scrobbleRepository, times(1))
            .findTopByUserNameOrderByTimeDesc("shicks255")
        verify(client, times(1))
            .getRecentTracks("shicks255", null, 12345679L, null)
        verify(client, times(1))
            .getRecentTracks("shicks255", 1, 12345679L, null)
        verify(dataLoadService, times(1))
            .startDataLoadTracking("shicks255", 1)
        verify(dataLoadService, times(1))
            .endDataLoadStatus("shicks255")
        verify(scrobbleRepository, times(1))
            .save(any())
        verifyNoMoreInteractions(scrobbleRepository)
        verifyNoMoreInteractions(client)
    }

    private fun createRecentTracks(): RecentTracks = RecentTracks(
        RecentTrack(
            attr = Attr(
                page = 1,
                total = 1,
                user = "shicks255",
                perPage = 200,
                totalPages = 1
            ),
            track = listOf(
                Track(
                    artist = Artist("123", text = "Pink Floyd"),
                    name = "Dogs",
                    album = Album("2", "Animals"),
                    date = Datee(1, "123"),
                    image = emptyList(),
                    mbid = "",
                    streamable = 1,
                    url = ""
                )
            )
        )
    )
}
