package com.steven.hicks.lastFmService.controllers.dtos.request

import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.ScrobbleField.ALBUM_MBID
import com.steven.hicks.lastFmService.entities.ScrobbleField.ARTIST_MBID
import com.steven.hicks.lastFmService.entities.ScrobbleField.ARTIST_NAME
import com.steven.hicks.lastFmService.entities.ScrobbleField.ID
import com.steven.hicks.lastFmService.entities.ScrobbleField.NAME
import com.steven.hicks.lastFmService.entities.ScrobbleField.TIME
import com.steven.hicks.lastFmService.entities.ScrobbleField.USER_NAME
import com.steven.hicks.lastFmService.entities.Table
import com.steven.hicks.lastFmService.entities.queryBuilding.Condition
import com.steven.hicks.lastFmService.entities.queryBuilding.Direction
import com.steven.hicks.lastFmService.entities.queryBuilding.QueryBuilder
import com.steven.hicks.lastFmService.entities.queryBuilding.WhereOperator.EQ
import com.steven.hicks.lastFmService.prepareStrQuery
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import com.steven.hicks.lastFmService.entities.ScrobbleField.ALBUM_NAME as ALBUM_NAME1

data class ScrobbleRequest(
    val userName: String,
    val artistName: String?,
    val albumName: String?,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val from: LocalDate?,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val to: LocalDate?,
    val limit: Int?,
    @Schema(
        allowableValues = [
            "ID",
            "ALBUM_MBID",
            "ALBUM_NAME",
            "ARTIST_MBID",
            "ARTIST_NAME",
            "TIME",
            "USER_NAME",
            "NAME"
        ]
    )
    val sort: ScrobbleField?,
    val direction: Direction?
) : QueryRequest {

    override fun buildQuery(): String {
        return QueryBuilder.build {
            select(ID) {
                and(ALBUM_NAME1)
                and(ALBUM_MBID)
                and(ARTIST_NAME)
                and(ARTIST_MBID)
                and(NAME)
                and(TIME)
                and(USER_NAME)
            }
            from(Table.SCROBBLE)
            where(Condition(USER_NAME, EQ, userName.prepareStrQuery().toLowerCase())) {
                if (!albumName.isNullOrEmpty())
                    and(Condition(ALBUM_NAME1, EQ, albumName.prepareStrQuery()))
                if (!artistName.isNullOrEmpty())
                    and(Condition(ARTIST_NAME, EQ, artistName.prepareStrQuery()))
                andTimeWhere(from, to)
            }
            sort(sort)
            order(direction)
            limit(limit)
        }
    }
}
