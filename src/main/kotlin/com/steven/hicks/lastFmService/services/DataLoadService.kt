package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.response.LoadStatusResponse
import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.entities.data.LoadStatus
import com.steven.hicks.lastFmService.repositories.DataLoadRepository
import com.steven.hicks.lastFmService.repositories.LoadStatusRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class DataLoadService(
    val dataLoadRepository: DataLoadRepository,
    val loadStatusRepository: LoadStatusRepository
) {

    companion object {
        const val MY_USERNAME = "shicks255"
        const val ONE_HUNDRED_PERCENT = 100
    }

    @Logged
    fun getMostRecentDataLoad(userName: String): DataLoad? {
        return dataLoadRepository.getLastSuccessfull(userName.toLowerCase())
    }

    @Logged
    fun getRunningOrErrorDataLoad(userName: String): DataLoad? {
        return dataLoadRepository.getFailedOrRunningDataLoad(userName.toLowerCase())
    }

    @Logged
    fun getRunningDataLoads(): List<DataLoad> {
        return dataLoadRepository.getRunningDataLoads()
    }

    @Logged
    fun createDataLoad(userName: String): DataLoad {
        val loadEvent = DataLoad(
            OffsetDateTime.now(), userName.toLowerCase(), DataLoadStatus.RUNNING, 0
        )

        dataLoadRepository.save(loadEvent)
        return loadEvent
    }

    @Logged
    fun saveDataLoad(dataLoad: DataLoad) {
        dataLoadRepository.save(dataLoad)
    }

    @Logged
    fun startDataLoadTracking(userName: String, totalPages: Int): LoadStatus {
        val newTracking = LoadStatus(
            userName = userName.toLowerCase(),
            totalPages = totalPages,
            currentPage = totalPages,
            timestamp = OffsetDateTime.now()
        )

        return loadStatusRepository.save(newTracking)
    }

    @Logged
    fun updateDataLoadStatus(userName: String, page: Int): LoadStatus? {
        val tracking = loadStatusRepository.findByIdOrNull(userName.toLowerCase())
        if (tracking != null) {
            tracking.currentPage = page
            tracking.timestamp = OffsetDateTime.now()
            return loadStatusRepository.save(tracking)
        }
        return null
    }

    @Logged
    fun endDataLoadStatus(userName: String) {
        loadStatusRepository.deleteById(userName.toLowerCase())
    }

    @Logged
    fun getDataLoadStatus(userName: String): LoadStatusResponse {
        val tracking = loadStatusRepository.findByIdOrNull(userName.toLowerCase())
        if (tracking != null) {

            var currentPage = tracking.totalPages - tracking.currentPage
            if (currentPage < 1) currentPage = 1
            val percentDone = currentPage.toDouble() / tracking.totalPages.toDouble()
            val percent = (percentDone * ONE_HUNDRED_PERCENT).toInt()
            val message = "$percent%  done.  Working on page $currentPage of ${tracking.totalPages}"

            return LoadStatusResponse(
                currentPage = currentPage,
                totalPages = tracking.totalPages,
                message = message,
                timestamp = tracking.timestamp
            )
        }

        return LoadStatusResponse(
            0, 0, "", OffsetDateTime.now()
        )
    }
}
