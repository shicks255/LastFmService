package com.steven.hicks.LastFmService

import com.steven.hicks.LastFmService.services.LastFmRestClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LastFmServiceApplication(val lastFmRestClient: LastFmRestClient): ApplicationRunner {
	override fun run(args: ApplicationArguments?) {
		lastFmRestClient.getRecentTracks()
	}
}

fun main(args: Array<String>) {
	runApplication<LastFmServiceApplication>(*args)
}




