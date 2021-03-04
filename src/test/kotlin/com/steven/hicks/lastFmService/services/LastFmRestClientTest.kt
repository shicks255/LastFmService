package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.dto.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.*
import java.net.URI
import reactor.core.publisher.Mono

import org.mockito.ArgumentMatchers.any

import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import org.springframework.web.util.UriBuilder
import kotlin.jvm.internal.Intrinsics
import org.mockito.ArgumentCaptor
import java.util.function.BiFunction
import java.util.function.Function


@ExtendWith(MockitoExtension::class)
class LastFmRestClientTest {

    @Mock
    lateinit var client: WebClient

    @Captor
    private var lambdaCaptor: ArgumentCaptor<java.util.function.Function<UriBuilder, URI>?>? = null

    @InjectMocks
    val sut = LastFmRestClient()

    @Test
    fun `should throw LastFMException when something goes wrong`() {

        `when`(client.get())
            .then { throw WebClientRequestException(Exception(), HttpMethod.GET, URI.create(""), HttpHeaders.EMPTY) }

        assertThatThrownBy { sut.getRecentTracks() }
            .isInstanceOf(LastFmException::class.java)
            .hasMessageContaining("Problem calling last FM")
    }

//    @Test
    fun `sadf`() {

        val uriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec::class.java)
        val headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec::class.java)
        val responseSpecMock = Mockito.mock(WebClient.ResponseSpec::class.java)
        val monoSpecMock = Mockito.mock(Mono::class.java)
//        val uri = Mockito.mock(URI::class.java)

//        var lam = {x: UriBuilder -> URI.create(anyString())}
//        val x = Mockito.mock(lam::class.java)

        doReturn(uriSpecMock)
            .`when`(client).get()
        doReturn(headersSpecMock)
            .`when`(uriSpecMock)
            .uri(anyString())
        doReturn(responseSpecMock)
            .`when`(uriSpecMock)
            .retrieve()
        doReturn(monoSpecMock)
            .`when`(responseSpecMock)
            .bodyToMono(RecentTracks::class.java)
        doReturn(createRecentTracks())
            .`when`(monoSpecMock)
            .block()

//        `when`(client.get()).thenReturn(uriSpecMock)
//        `when`(uriSpecMock.uri(Function<>))).thenReturn(headersSpecMock)
//        `when`(headersSpecMock.retrieve()).thenReturn(responseSpecMock)
//        `when`(responseSpecMock.bodyToMono(ArgumentMatchers.notNull<Class<RecentTracks>>()))
//            .thenReturn(Mono.just(createRecentTracks()))

        val tracks = sut.getRecentTracks()

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