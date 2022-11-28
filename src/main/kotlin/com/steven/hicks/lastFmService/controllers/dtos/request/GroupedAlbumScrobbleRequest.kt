package com.steven.hicks.lastFmService.controllers.dtos.request

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.entities.ScrobbleField.ALBUM_NAME
import com.steven.hicks.lastFmService.entities.ScrobbleField.ARTIST_NAME
import com.steven.hicks.lastFmService.entities.ScrobbleField.COUNT_STAR
import com.steven.hicks.lastFmService.entities.ScrobbleField.USER_NAME
import com.steven.hicks.lastFmService.entities.Table.SCROBBLE
import com.steven.hicks.lastFmService.entities.queryBuilding.Condition
import com.steven.hicks.lastFmService.entities.queryBuilding.QueryBuilder
import com.steven.hicks.lastFmService.entities.queryBuilding.WhereOperator
import com.steven.hicks.lastFmService.entities.queryBuilding.WhereOperator.EQ
import com.steven.hicks.lastFmService.entities.queryBuilding.WhereOperator.IN
import com.steven.hicks.lastFmService.prepareStrQuery
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class GroupedAlbumScrobbleRequest(
    val userName: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val from: LocalDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val to: LocalDate,
    val albumNames: List<String>?,
    val artistNames: List<String>?,
    val timeGroup: TimeGroup,
    val limit: Int?,
    val empties: Boolean? = false
) : QueryRequest {

    override fun buildQuery(): String {
        return QueryBuilder.build {
            select(COUNT_STAR) {
                and(getTimeGroup(timeGroup))
                and(ALBUM_NAME)
                and(ARTIST_NAME)
            }
            from(SCROBBLE)
            where(Condition(ALBUM_NAME, WhereOperator.NE, "''")) {
                and(Condition(USER_NAME, EQ, userName.prepareStrQuery().toLowerCase()))
                if (!albumNames.isNullOrEmpty()) {
                    val inPieces =
                        albumNames.joinToString(separator = ",", prefix = "(", postfix = ")") { it.prepareStrQuery() }
                    and(Condition(ALBUM_NAME, IN, inPieces))
                }
                if (!artistNames.isNullOrEmpty()) {
                    val inPieces =
                        artistNames.joinToString(separator = ",", prefix = "(", postfix = ")") { it.prepareStrQuery() }
                    and(Condition(ARTIST_NAME, IN, inPieces))
                }
                andTimeWhere(from, to)
            }
            groupBy(listOf(getTimeGroup(timeGroup), ALBUM_NAME, ARTIST_NAME))
        }
    }
}
