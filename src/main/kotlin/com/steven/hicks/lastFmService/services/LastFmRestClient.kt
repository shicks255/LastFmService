package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientException

@Component
class LastFmRestClient {

    val logger = LoggerFactory.getLogger(LastFmRestClient::class.java)

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

            recentTracks ?: throw LastFmException("Problem calling last FM")
        } catch (e: WebClientException) {
            logger.error("Problem calling last FM ${e.localizedMessage}")
            throw LastFmException("Problem calling last FM", e)
        }
    }

    @Logged
    private fun createUrl(userName: String, page: Int? = null, from: Long? = null, to: Long? = null): String {
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
