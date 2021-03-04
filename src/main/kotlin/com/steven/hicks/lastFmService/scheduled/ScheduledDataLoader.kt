package com.steven.hicks.lastFmService.scheduled

import com.steven.hicks.lastFmService.services.DataLoadService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ScheduledDataLoader(val dataLoadService: DataLoadService) {

    //Every morning at 3
    @Scheduled(cron = "0 0 3 * * *")
//    @Scheduled(initialDelay = 1000, fixedDelay = 1000000)
    fun loadDay() {
        //log
        println("Loading ${LocalDate.now().minusDays(1)}")
        dataLoadService.performDataLoad()
    }
}
