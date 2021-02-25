package com.steven.hicks.LastFmService.services

import com.steven.hicks.LastFmService.entities.LastFmException
import com.steven.hicks.LastFmService.entities.dto.RecentTracks
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
        val LAST_FM_URL = "https://ws.audioscrobbler.com/2.0/?"
    }

    fun getRecentTracks(
            page: Int? = null,
            from: Long? = null,
            to: Long? = null
    ): RecentTracks {

        val pageParam = if (page != null) "&page=$page" else ""

        val url = "${LAST_FM_URL}method=user.getrecenttracks&user=${lastFmDefaultUser}&limit=200&format=json&api_key=${lastFmKey}$pageParam"
        val client = WebClient.create()

        return try {
            val recentTracks = client.get()
                    .uri(url)
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