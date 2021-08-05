package com.steven.hicks.lastFmService.controllers.dtos.request

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.ScrobbleField.TIMEGROUP_DAY
import com.steven.hicks.lastFmService.entities.ScrobbleField.TIMEGROUP_MONTH
import com.steven.hicks.lastFmService.entities.ScrobbleField.TIMEGROUP_WEEK
import com.steven.hicks.lastFmService.entities.ScrobbleField.TIMEGROUP_YEAR

interface QueryRequest {

    fun buildQuery(): String
    fun getTimeGroup(timeGroup: TimeGroup): ScrobbleField {
        return when (timeGroup) {
            TimeGroup.WEEK -> TIMEGROUP_WEEK
            TimeGroup.MONTH -> TIMEGROUP_MONTH
            TimeGroup.YEAR -> TIMEGROUP_YEAR
            TimeGroup.DAY -> TIMEGROUP_DAY
        }
    }
}
