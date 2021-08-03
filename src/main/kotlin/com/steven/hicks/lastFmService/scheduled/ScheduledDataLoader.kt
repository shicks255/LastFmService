package com.steven.hicks.lastFmService.scheduled

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.services.DataLoadService
import com.steven.hicks.lastFmService.services.LastFmLoadingService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
@Profile("prod")
class ScheduledDataLoader(
    val dataLoadService: DataLoadService,
    val lastFmLoadingService: LastFmLoadingService,
) {

    companion object {
        const val FIVE_MINUTES = (1_000 * 60 * 5).toLong()
        const val TWELVE_HOURS = (1_000 * 60 * 60 * 12).toLong()
    }

    val logger = LoggerFactory.getLogger(ScheduledDataLoader::class.java)

    //Every 12 hours
    @Scheduled(initialDelay = FIVE_MINUTES, fixedDelay = TWELVE_HOURS)
    @Logged
    fun loadDay() {

        val time = measureTimeMillis {
            logger.info("Starting scheduled data load")
            val loadEvent = dataLoadService.createDataLoad()
            var finishedEvent: DataLoad? = null
            try {
                val result = lastFmLoadingService.loadRecent(DataLoadService.MY_USERNAME)
                finishedEvent = loadEvent.copy(
                    status = DataLoadStatus.SUCCESS,
                    count = result
                )
            } catch (e: Exception) {
                logger.info(e.message)
                finishedEvent = loadEvent.copy(
                    status = DataLoadStatus.ERROR,
                    count = 0
                )
            }

            dataLoadService.saveDataLoad(finishedEvent!!)
        }
        logger.info("Finished scheduled data load in $time")
    }
}
