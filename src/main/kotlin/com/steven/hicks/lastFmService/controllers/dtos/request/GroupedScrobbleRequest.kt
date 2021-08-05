package com.steven.hicks.lastFmService.controllers.dtos.request

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.Table
import com.steven.hicks.lastFmService.entities.queryBuilding.Condition
import com.steven.hicks.lastFmService.entities.queryBuilding.QueryBuilder
import com.steven.hicks.lastFmService.entities.queryBuilding.WhereOperator.EQ
import com.steven.hicks.lastFmService.prepareStrQuery
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class GroupedScrobbleRequest(
    val userName: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val from: LocalDate?,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val to: LocalDate?,
    val timeGroup: TimeGroup
) : QueryRequest {

    override fun buildQuery(): String {
        return QueryBuilder.build {
            select(ScrobbleField.COUNT_STAR) {
                and(getTimeGroup(timeGroup))
            }
            from(Table.SCROBBLE)
            where(Condition(ScrobbleField.USER_NAME, EQ, userName.prepareStrQuery())) {
                andTimeWhere(from, to)
            }
            groupBy(listOf(getTimeGroup(timeGroup)))
        }
    }
}
