package com.steven.hicks.lastFmService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.concurrent.Executor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@SpringBootApplication
@EnableScheduling
@EnableAsync
class LastFmServiceApplication

fun main(args: Array<String>) {
    runApplication<LastFmServiceApplication>(*args)
}

@Bean
@Suppress("MagicNumber")
fun taskExecutor(): Executor {
    val executor = ThreadPoolTaskExecutor()
    executor.corePoolSize = 2
    executor.maxPoolSize = 4
    executor.setThreadNamePrefix("ThreadPool-")
    executor.initialize()
    return executor
}
