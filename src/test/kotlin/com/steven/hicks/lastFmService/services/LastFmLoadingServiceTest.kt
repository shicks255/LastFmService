package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.dto.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class LastFmLoadingServiceTest {

    @Mock
    lateinit var client: LastFmRestClient

    @Mock
    lateinit var scrobbleService: ScrobbleService

    @InjectMocks
    lateinit var sut: LastFmLoadingService

    @Test
    fun `should loadDay`() {

        val date = LocalDate.now()

        `when`(client.getRecentTracks(from = any(), to = any(), page = eq(null)))
            .thenReturn(createRecentTracks())
        `when`(client.getRecentTracks(any(), any(), any()))
            .thenReturn(createRecentTracks())

        sut.loadDay(date)

        verify(client, times(1)).getRecentTracks(eq(null), any(), any())
        verify(client, times(2)).getRecentTracks(any(), any(), anyLong())
        verify(scrobbleService, times(1)).saveRecentTrack(createRecentTracks().recenttracks.track.first())
    }

    @Test
    fun `should loadAll`() {
        `when`(client.getRecentTracks(eq(1), eq(null), eq(null)))
            .thenReturn(createRecentTracks())

        sut.loadAll()

        verify(client, times(1)).getRecentTracks(eq(1), eq(null), eq(null))
        verify(scrobbleService, times(1)).saveRecentTrack(createRecentTracks().recenttracks.track.first())
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

    fun createRecentTracksMultiTracks(): RecentTracks = RecentTracks(
        RecentTrack(
            attr = Attr(
                page = 1,
                total = 1,
                user = "shicks255",
                perPage = 200,
                totalPages = 3
            ),
            track = listOf(
                createTrack(), createTrack(), createTrack()
            )
        )
    )

    fun createTrack(): Track = Track(
        artist = Artist("123", text = "Pink Floyd"),
        name = "Dogs",
        album = Album("2", "Animals"),
        date = Datee(1, "123"),
        image = emptyList(),
        mbid = "",
        streamable = 1,
        url = ""
    )

}