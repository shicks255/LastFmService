package com.steven.hicks.LastFmService.services

import com.steven.hicks.LastFmService.entities.dto.RecentTracks
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

@Component
class LastFmRestClient(
        val restTemplate: RestTemplate
) {

    @Value("\${lastfm.user}")
    private lateinit var lastFmDefaultUser: String
    @Value("\${lastfm.apiKey}")
    private lateinit var lastFmKey: String

    companion object {
        val LAST_FM_URL = "https://ws.audioscrobbler.com/2.0/?"
    }

    open fun getRecentTracks() {

        val url = "${LAST_FM_URL}method=user.getrecenttracks&user=${lastFmDefaultUser}&limit=200&format=json&api_key=${lastFmKey}"
        val client = WebClient.create()

        val recentTracks = client.get()
                .uri(url)
                .retrieve()
                .bodyToMono(RecentTracks::class.java)
                .block()

        println(recentTracks);
    }


}