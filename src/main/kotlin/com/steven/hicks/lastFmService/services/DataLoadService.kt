package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.repositories.DataLoadRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime

@Service
class DataLoadService(
        val repository: DataLoadRepository,
        val lastFmLoadingService: LastFmLoadingService
) {

    fun performDataLoad(date: LocalDate = LocalDate.now().minusDays(1)) {

        val loadEvent = DataLoad(
                date, OffsetDateTime.now(), DataLoadStatus.RUNNING, 0)

        repository.save(loadEvent)
        try {
            val result = lastFmLoadingService.loadDay(date)
            val finishedEvent = loadEvent.copy(
                    status = DataLoadStatus.SUCCESS,
                    count = result
            )
            repository.save(finishedEvent)
        } catch (e: LastFmException) {
            val finishedEvent = loadEvent.copy(
                    status = DataLoadStatus.ERROR,
                    error = e.message
            )
            repository.save(finishedEvent);
            //log
            println(e.localizedMessage)
        }

    }

}