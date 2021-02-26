package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.dto.RecentTracks
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class LastFmRestClient {

    @Value("\${lastfm.user}")
    private lateinit var lastFmDefaultUser: String

    @Value("\${lastfm.apiKey}")
    private lateinit var lastFmKey: String

    companion object {
        val LAST_FM_URL = "https://ws.audioscrobbler.com"
    }

    fun getRecentTracks(
            page: Int? = null,
            from: Long? = null,
            to: Long? = null
    ): RecentTracks {

        val client = WebClient
                .builder()
                .baseUrl(LAST_FM_URL)
                .build()
        return try {
            val recentTracks = client.get()
                    .uri { uri ->
                        uri.path("/2.0/")
                                .queryParam("method", "user.getrecenttracks")
                                .queryParam("user", lastFmDefaultUser)
                                .queryParam("limit", 200)
                                .queryParam("format", "json")
                                .queryParam("api_key", lastFmKey)
                        if (page != null)
                            uri.queryParam("page", page)
                        if (from != null)
                            uri.queryParam("from", from)
                        if (to != null)
                            uri.queryParam("to", to)

                        uri.build()
                    }
                    .retrieve()
                    .bodyToMono(RecentTracks::class.java)
                    .block()

            recentTracks ?: throw Exception("")
        } catch (e: Exception) {
            println(e);
            throw LastFmException("Problem calling last FM", e)
        }
    }
}