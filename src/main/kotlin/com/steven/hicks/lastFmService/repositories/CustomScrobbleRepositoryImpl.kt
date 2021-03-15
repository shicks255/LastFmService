package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.*
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.entities.data.Scrobble
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneOffset
import javax.persistence.EntityManager

@Repository
@Transactional(readOnly = true)
class CustomScrobbleRepositoryImpl(
    val entityManager: EntityManager
) : CustomScrobbleRepository {

    override fun suggestAlbums(typed: String): List<String> {
        return entityManager
            .createNativeQuery("select distinct album_name from scrobble where lower(album_name) like '%${typed.toLowerCase()}%'  order by album_name asc")
            .resultList as List<String>
    }

    override fun suggestArtists(typed: String): List<String> {
        return entityManager
            .createNativeQuery("select distinct artist_name from scrobble where lower(artist_name) like '%${typed.toLowerCase()}%'  order by artist_name asc")
            .resultList as List<String>    }

    override fun getScrobbles(request: ScrobbleRequest): List<Scrobble> {
        val query = entityManager.createNativeQuery(buildQuery(request), Scrobble::class.java)
        return query.resultList as List<Scrobble>
    }

    override fun getScrobbles(request: GroupedArtistScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(buildQuery(request))
        return query.resultList as List<Any>
    }

    override fun getScrobbles(request: GroupedAlbumScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(buildQuery(request))
        return query.resultList as List<Any>
    }

    override fun getScrobbles(request: GroupedScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(buildQuery(request))
        return query.resultList as List<Any>
    }

    fun buildQuery(request: GroupedScrobbleRequest): String {
        val query = "select count(*), ${getTimeGroup(request.timeGroup)} from scrobble " +
                "where 1 = 1"

        val where = includeTimeClause("", request.from, request.to)

        val groupBy = " group by ${getTimeGroup(request.timeGroup)}"

        return query + where + groupBy
    }

    fun buildQuery(scrobbleRequest: GroupedAlbumScrobbleRequest): String {
        val query = "select count(*), ${getTimeGroup(scrobbleRequest.timeGroup)} from scrobble " +
                "where 1 = 1"

        val whereClause = with(scrobbleRequest) {
            var where = ""
            if (!scrobbleRequest.albumNames.isNullOrEmpty())
                where += " and album_name in (${scrobbleRequest.albumNames.joinToString(",", "'", "'")})"
            includeTimeClause(where, scrobbleRequest.from, scrobbleRequest.to)
        }

        val groupBy = " group by ${getTimeGroup(scrobbleRequest.timeGroup)}"

        return query + whereClause + groupBy
    }

    fun buildQuery(scrobbleRequest: GroupedArtistScrobbleRequest): String {
        val query = "select count(*), ${getTimeGroup(scrobbleRequest.timeGroup)} from scrobble " +
                "where 1 = 1 "

        val whereClause = with(scrobbleRequest) {
            var where = ""
            if (!scrobbleRequest.artistNames.isNullOrEmpty())
                where += " and artist_name in (${scrobbleRequest.artistNames.joinToString(",", "'", "'")})"
            includeTimeClause(where, scrobbleRequest.from, scrobbleRequest.to)
        }

        val groupBy = " group by ${getTimeGroup(scrobbleRequest.timeGroup)}"

        return query + whereClause + groupBy
    }

    fun includeTimeClause(where: String, from: LocalDate?, to: LocalDate?): String {
        var wheree = where
        if (from != null) {
            val fromm = from.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(-6))
//            val fromm = ZonedDateTime.of(from.atStartOfDay(), ZoneId.of("UTC")).toEpochSecond()
            wheree += " and time >= $fromm "
        }
        if (to != null) {
            val too = to.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(-6))
//            val too = ZonedDateTime.of(to.atStartOfDay(), ZoneId.of("UTC")).toEpochSecond()
            wheree += " and time < $too "
        }

        return wheree
    }

    fun getTimeGroup(timeGroup: TimeGroup): String {
        return when (timeGroup) {
            TimeGroup.WEEK -> "to_char(to_timestamp(time), 'IYYY-IW')"
            TimeGroup.MONTH -> "to_char(to_timestamp(time), 'IYYY-MM')"
            TimeGroup.YEAR -> "to_char(to_timestamp(time), 'YYYY')"
            TimeGroup.DAY -> "to_char(to_timestamp(time), 'YYYY-MM-DD')"
        }
    }

    fun buildQuery(scrobbleRequest: ScrobbleRequest): String {
        val query =
            "select id, album_name, album_mbid, artist_name, artist_mbid, name, time " +
                    "from scrobble where 1 = 1 "

        val whereClause = with(scrobbleRequest) {
            var where = ""
            if (!scrobbleRequest.albumName.isNullOrBlank())
                where += " and album_name=\'${scrobbleRequest.albumName}\'"
            if (!scrobbleRequest.artistName.isNullOrBlank())
                where += " and artist_name=\'${scrobbleRequest.artistName}\'"
            includeTimeClause(where, scrobbleRequest.from, scrobbleRequest.to)
        }

        val sortBy = with(scrobbleRequest) {
            var order = ""
            if (scrobbleRequest.sort != null) {
                order = " order by " + scrobbleRequest.sort
            }
            order
        }

        return query + whereClause + sortBy
    }

}