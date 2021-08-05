package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.dto.Album
import com.steven.hicks.lastFmService.entities.dto.Artist
import com.steven.hicks.lastFmService.entities.dto.Attr
import com.steven.hicks.lastFmService.entities.dto.Datee
import com.steven.hicks.lastFmService.entities.dto.RecentTrack
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import com.steven.hicks.lastFmService.entities.dto.Track
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import reactor.core.publisher.Mono
import java.net.URI

@ExtendWith(MockitoExtension::class)
class LastFmRestClientTest {

    @Mock
    lateinit var client: WebClient

    @InjectMocks
    val sut = LastFmRestClient()

    init {
        sut.lastFmDefaultUser = ""
        sut.lastFmKey = ""
    }

    @Test
    fun `should throw LastFMException when something goes wrong`() {

        `when`(client.get())
            .then { throw WebClientRequestException(Exception(), HttpMethod.GET, URI.create(""), HttpHeaders.EMPTY) }

        assertThatThrownBy { sut.getRecentTracks("shicks255") }
            .isInstanceOf(LastFmException::class.java)
            .hasMessageContaining("Problem calling last FM")
    }

    @Test
    fun `should return lastFm results`() {

        val uriSpecMock = mock(WebClient.RequestHeadersUriSpec::class.java)
        val headersSpecMock = mock(WebClient.RequestHeadersSpec::class.java)
        val responseSpecMock = mock(WebClient.ResponseSpec::class.java)
        val monoSpecMock = mock(Mono::class.java)

        doReturn(uriSpecMock)
            .`when`(client).get()
        doReturn(headersSpecMock)
            .`when`(uriSpecMock)
            .uri(anyString())
        doReturn(responseSpecMock)
            .`when`(headersSpecMock)
            .retrieve()
        doReturn(monoSpecMock)
            .`when`(responseSpecMock)
            .bodyToMono(RecentTracks::class.java)
        doReturn(createRecentTracks())
            .`when`(monoSpecMock)
            .block()

        val tracks = sut.getRecentTracks("shicks255", 1, 2, 3)

        assertThat(tracks).isEqualTo(createRecentTracks())
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
