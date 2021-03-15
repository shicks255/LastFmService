package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.repositories.DataLoadRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class DataLoadService(
        val repository: DataLoadRepository,
        val lastFmLoadingService: LastFmLoadingService
) {

    val logger = LoggerFactory.getLogger(DataLoadService::class.java)

    fun performDataLoad() {

        val loadEvent = DataLoad(
                OffsetDateTime.now(), DataLoadStatus.RUNNING, 0)

        repository.save(loadEvent)
        try {
            val result = lastFmLoadingService.loadRecent()
            val finishedEvent = loadEvent.copy(
                    status = DataLoadStatus.SUCCESS,
                    count = result
            )
            repository.save(finishedEvent)
            logger.info("Saved $result scrobbles from LastFM")
        } catch (e: LastFmException) {
            val finishedEvent = loadEvent.copy(
                    status = DataLoadStatus.ERROR,
                    error = e.message
            )
            repository.save(finishedEvent);
            logger.error("Error trying to load lastFM data: ${e.localizedMessage}")
        }
    }
}
