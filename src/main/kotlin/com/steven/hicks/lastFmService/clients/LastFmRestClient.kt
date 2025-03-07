package com.steven.hicks.lastFmService.clients

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import io.netty.resolver.DefaultAddressResolverGroup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Component
class LastFmRestClient {

    val logger: Logger = LoggerFactory.getLogger(LastFmRestClient::class.java)

    var client: WebClient = WebClient
        .builder()
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient.create().resolver(
                    DefaultAddressResolverGroup.INSTANCE
                )
            )
        )
        .baseUrl(LAST_FM_URL)
        .build()

    var lastFmKey: String = System.getenv("lastfm.apiKey") ?: ""
    companion object {
        const val LAST_FM_URL = "https://ws.audioscrobbler.com"
        const val PAGE_LIMIT = 200
        const val LAST_FM_EXCEPTION_CODE = 5000
    }

    @Logged
    fun getRecentTracks(
        userName: String,
        page: Int? = null,
        from: Long? = null,
        to: Long? = null
    ): RecentTracks {

        return try {
            logger.info("Calling ${createUrl(userName, page, from, to)}")
            val recentTracks = client.get()
                .uri(createUrl(userName, page, from, to))
                .retrieve()
                .bodyToMono(RecentTracks::class.java)
                .block()

            recentTracks!!
        } catch (e: Exception) {
            logger.error("Problem calling last FM ${e.localizedMessage}, ${e.stackTraceToString()}")
            throw LastFmException(LAST_FM_EXCEPTION_CODE, "Problem calling last FM", e)
        }
    }

    @Logged
    fun createUrl(userName: String, page: Int? = null, from: Long? = null, to: Long? = null): String {
        var url =
            "/2.0/?method=user.getrecenttracks&user=$userName&limit=$PAGE_LIMIT&format=json&api_key=$lastFmKey"

        if (page != null)
            url += "&page=$page"
        if (from != null)
            url += "&from=$from"
        if (to != null)
            url += "&to=$to"

        return url
    }
}
