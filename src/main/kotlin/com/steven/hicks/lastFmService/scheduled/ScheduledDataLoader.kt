package com.steven.hicks.lastFmService.scheduled

import com.steven.hicks.lastFmService.services.DataLoadService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
@Profile("prod")
class ScheduledDataLoader(val dataLoadService: DataLoadService) {

    companion object {
        const val FIVE_MINUTES = (1_000 * 60 * 5).toLong()
        const val TWELVE_HOURS = (1_000 * 60 * 60 * 12).toLong()
    }

    val logger = LoggerFactory.getLogger(ScheduledDataLoader::class.java)

    //Every 6 hours
    @Scheduled(initialDelay = FIVE_MINUTES, fixedDelay = TWELVE_HOURS)
    fun loadDay() {

        val time = measureTimeMillis {
            logger.info("Starting scheduled data load")
            dataLoadService.performDataLoad()
        }
        logger.info("Finished scheduled data load in $time")
    }
}
