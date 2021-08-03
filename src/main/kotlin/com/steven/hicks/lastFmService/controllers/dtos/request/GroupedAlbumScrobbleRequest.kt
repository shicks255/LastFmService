package com.steven.hicks.lastFmService.controllers.dtos.request

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.Table
import com.steven.hicks.lastFmService.entities.queryBuilding.Condition
import com.steven.hicks.lastFmService.entities.queryBuilding.QueryBuilder
import com.steven.hicks.lastFmService.entities.queryBuilding.WhereOperator
import com.steven.hicks.lastFmService.prepareStrQuery
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class GroupedAlbumScrobbleRequest(
    val userName: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val from: LocalDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val to: LocalDate?,
    val albumNames: List<String>?,
    val timeGroup: TimeGroup,
    val limit: Int?,
    val empties: Boolean? = false
) : QueryRequest {

    override fun buildQuery(): String {
        return QueryBuilder.build {
            select(ScrobbleField.COUNT_STAR) {
                and(getTimeGroup(timeGroup))
                and(ScrobbleField.ALBUM_NAME)
                and(ScrobbleField.ARTIST_NAME)
            }
            from(Table.SCROBBLE)
            where(Condition(ScrobbleField.ALBUM_NAME, WhereOperator.NE, "''")) {
                and(Condition(ScrobbleField.USER_NAME, WhereOperator.EQ, userName.prepareStrQuery()))
                if (!albumNames.isNullOrEmpty()) {
                    val inPieces =
                        albumNames.joinToString(separator = ",", prefix = "(", postfix = ")") { it.prepareStrQuery() }
                    and(Condition(ScrobbleField.ALBUM_NAME, WhereOperator.IN, inPieces))
                }
                andTimeWhere(from, to)
            }
            groupBy(listOf(getTimeGroup(timeGroup), ScrobbleField.ALBUM_NAME, ScrobbleField.ARTIST_NAME))
            limit(limit)
        }
    }
}
