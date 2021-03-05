package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientException
import java.net.URI

@Component
class LastFmRestClient {

    var client: WebClient = WebClient
        .builder()
        .baseUrl(LAST_FM_URL)
        .build()

    @Value("\${lastfm.user}")
    lateinit var lastFmDefaultUser: String

    @Value("\${lastfm.apiKey}")
    lateinit var lastFmKey: String

    companion object {
        const val LAST_FM_URL = "https://ws.audioscrobbler.com"
        const val PAGE_LIMIT = 200
    }

    fun getRecentTracks(
        page: Int? = null,
        from: Long? = null,
        to: Long? = null
    ): RecentTracks {

        return try {
            val recentTracks = client.get()
                .uri(createUrl(page, from, to))
                .retrieve()
                .bodyToMono(RecentTracks::class.java)
                .block()

            recentTracks ?: throw LastFmException("Problem calling last FM")
        } catch (e: WebClientException) {
            println(e);
            throw LastFmException("Problem calling last FM", e)
        }
    }

    private fun createUrl(page: Int? = null, from: Long? = null, to: Long? = null): String =
        URI.create(
            "/2.0/?method=user.getrecenttracks&user=$lastFmDefaultUser&limit=$PAGE_LIMIT&format=json&api_key=$lastFmKey"
                .also {
                    if (page != null)
                        it.plus("&page=$page")
                    if (from != null)
                        it.plus("&from=$from")
                    if (to != null)
                        it.plus("&to=$to")
                }
        ).toString()
}
