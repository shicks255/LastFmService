package com.steven.hicks.lastFmService

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@SpringBootApplication
@EnableScheduling
@EnableAsync
class LastFmServiceApplication {

    companion object {
        const val CORE_POOL = 2
        const val MAX_POOL = 4
    }

    fun main(args: Array<String>) {
        runApplication<LastFmServiceApplication>(*args)
    }

    @Bean
    fun taskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = CORE_POOL
        executor.maxPoolSize = MAX_POOL
        executor.setThreadNamePrefix("ThreadPool-")
        executor.initialize()
        return executor
    }
}
