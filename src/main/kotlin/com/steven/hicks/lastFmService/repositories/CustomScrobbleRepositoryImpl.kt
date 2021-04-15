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

    companion object {
        const val UTC_OFFSET = -6
    }

    override fun suggestAlbums(typed: String): List<String> {
        return entityManager
            .createNativeQuery("select distinct " +
                    "album_name from scrobble where lower(album_name) " +
                    "like '%${typed.toLowerCase()}%'  order by album_name asc")
            .resultList as List<String>
    }

    override fun suggestArtists(typed: String): List<String> {
        return entityManager
            .createNativeQuery("select distinct artist_name from scrobble " +
                    "where lower(artist_name) like '%${typed.toLowerCase()}%'  " +
                    "order by artist_name asc")
            .resultList as List<String>    }

    override fun getScrobbles(request: ScrobbleRequest): List<Scrobble> {
        val query = entityManager.createNativeQuery(buildQuery(request), Scrobble::class.java)
        return query.resultList as List<Scrobble>
    }

    override fun getArtistGroupedScrobbles(request: GroupedArtistScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(buildArtistGroupedQuery(request))
        return query.resultList as List<Any>
    }

    override fun getAlbumGroupedScrobbles(request: GroupedAlbumScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(buildAlbumGroupedQuery(request))
        return query.resultList as List<Any>
    }

    override fun getGroupedScrobbles(request: GroupedScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(buildGroupedQuery(request))
        return query.resultList as List<Any>
    }

    fun buildGroupedQuery(request: GroupedScrobbleRequest): String {
        val query = "select count(*), ${getTimeGroup(request.timeGroup)} from scrobble " +
                "where 1 = 1"
        val where = includeTimeClause("", request.from, request.to)
        val groupBy = " group by ${getTimeGroup(request.timeGroup)}"
        return query + where + groupBy
    }

    fun buildAlbumGroupedQuery(scrobbleRequest: GroupedAlbumScrobbleRequest): String {
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

    fun buildArtistGroupedQuery(scrobbleRequest: GroupedArtistScrobbleRequest): String {
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
            val fromm = from.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(UTC_OFFSET))
            wheree += " and time >= $fromm "
        }
        if (to != null) {
            val too = to.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(UTC_OFFSET))
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

    override fun getArtistWithOldestAndNewestPlay(userName: String): List<Any> {
        val query =
            "select artist_name, max(time), min(time), " +
                    "max(time)-min(time) as rang from scrobble " +
                    "group by artist_name order by rang desc limit 1"

        val queryr = entityManager.createNativeQuery(query)
        val finalthing = queryr.resultList as List<Any>
        return finalthing
    }

    override fun getArtistWithLongestDormancy(userName: String): List<Any> {
        val query =
            "select artist_name, time, " +
                    "lag(time) over (partition by artist_name order by time) as pp, " +
                    "time - lag(time) over (partition by artist_name order by time) as last_play " +
                    "from scrobble order by last_play desc nulls last;"

        val queryy = entityManager.createNativeQuery(query)
        val final = queryy.resultList as List<Any>
        return final
    }
}
