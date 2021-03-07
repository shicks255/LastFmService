package com.steven.hicks.lastFmService.scheduled

import com.steven.hicks.lastFmService.services.DataLoadService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.system.measureTimeMillis

@Service
class ScheduledDataLoader(val dataLoadService: DataLoadService) {

    val logger = LoggerFactory.getLogger(ScheduledDataLoader::class.java)

    //Every morning at 3
    @Scheduled(cron = "0 0 3 * * *")
//    @Scheduled(initialDelay = 1000, fixedDelay = 10000)
    fun loadDay() {

        val time = measureTimeMillis {
            val dayToLoad = LocalDate.now().minusDays(1)
            logger.info("Starting scheduled data load for $dayToLoad")
            dataLoadService.performDataLoad()
        }
        logger.info("Finished scheduled data load for in $time")
    }
}
