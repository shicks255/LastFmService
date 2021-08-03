package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.entities.data.Scrobble
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
@Transactional(readOnly = true)
class CustomScrobbleRepositoryImpl(
    val entityManager: EntityManager
) : CustomScrobbleRepository {

    companion object {
        const val UTC_OFFSET = -6
    }

    override fun suggestAlbums(userName: String, typed: String): List<String> {
        return entityManager
            .createNativeQuery(
                "select distinct " +
                        "album_name from SCROBBLE where user_name = '$userName' and lower(album_name) " +
                        "like '%${typed.toLowerCase()}%'  order by album_name asc"
            )
            .resultList as List<String>
    }

    override fun suggestArtists(userName: String, typed: String): List<String> {
        return entityManager
            .createNativeQuery(
                "select distinct artist_name from SCROBBLE " +
                        "where user_name = '$userName' and lower(artist_name) like '%${typed.toLowerCase()}%'  " +
                        "order by artist_name asc"
            )
            .resultList as List<String>
    }

    override fun getScrobbles(request: ScrobbleRequest): List<Scrobble> {
        val query = entityManager.createNativeQuery(request.buildQuery(), Scrobble::class.java)
        return query.resultList as List<Scrobble>
    }

    override fun getArtistGroupedScrobbles(request: GroupedArtistScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(request.buildQuery())
        return query.resultList as List<Any>
    }

    override fun getAlbumGroupedScrobbles(request: GroupedAlbumScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(request.buildQuery())
        return query.resultList as List<Any>
    }

    override fun getGroupedScrobbles(request: GroupedScrobbleRequest): List<Any> {
        val query = entityManager.createNativeQuery(request.buildQuery())
        return query.resultList as List<Any>
    }

    override fun getOldestAndNewestPlay(userName: String, type: String): List<Any> {
        val extraQuery = if (type == "album_name") ", artist_name " else ""

        val query =
            "select $type, max(time), min(time), " +
                    "max(time)-min(time) as rang " +
                    extraQuery +
                    "from scrobble " +
                    "where user_name = '$userName' " +
                    "group by $type $extraQuery" +
                    "order by rang desc limit 1"

        return entityManager.createNativeQuery(query).singleResult as List<Any>
    }

    override fun getLongestDormancy(userName: String, type: String): List<Any> {
        val extraQuery = if (type == "album_name") ", artist_name " else ""

        val query =
            "select $type, time, " +
                    "lag(time) over (partition by $type order by time) as pp, " +
                    "time - lag(time) over (partition by $type order by time) as last_play " +
                    extraQuery +
                    "from scrobble " +
                    "where user_name = '$userName' " +
                    "order by last_play desc nulls last;"

        return entityManager.createNativeQuery(query).singleResult as List<Any>
    }
}
