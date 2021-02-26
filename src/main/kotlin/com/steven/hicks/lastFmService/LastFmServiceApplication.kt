package com.steven.hicks.lastFmService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class LastFmServiceApplication

fun main(args: Array<String>) {
	runApplication<LastFmServiceApplication>(*args)
}
