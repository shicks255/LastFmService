package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRunningTotalRequest
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedAlbumResultMapper
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedArtistResultMapper
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedResultMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import javax.persistence.EntityManager

@Suppress("MagicNumber")
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
                    "album_name from SCROBBLE where user_name = '${userName.toLowerCase()}' and lower(album_name) " +
                    "like '%${typed.toLowerCase()}%'  order by album_name"
            )
            .resultList as List<String>
    }

    override fun suggestArtists(userName: String, typed: String): List<String> {
        return entityManager
            .createNativeQuery(
                "select distinct artist_name from SCROBBLE " +
                    "where user_name = '${userName.toLowerCase()}' and lower(artist_name) " +
                    "like '%${typed.toLowerCase()}%' order by artist_name"
            )
            .resultList as List<String>
    }

    override fun getScrobbles(request: ScrobbleRequest): List<Scrobble> {
        val query = entityManager.createNativeQuery(request.buildQuery(), Scrobble::class.java)
        return query.resultList as List<Scrobble>
    }

    override fun getArtistGroupedScrobbles(request: GroupedArtistScrobbleRequest): List<GroupedArtistResultMapper> {
        val query = entityManager.createNativeQuery(request.buildQuery())
        return query.resultList.map {
            val rowItemList = it as Array<*>
            val count = (rowItemList[0] as BigInteger).toInt()
            val timeGroup = rowItemList[1] as String
            val artist = rowItemList[2] as String
            GroupedArtistResultMapper(count, timeGroup, artist)
        }
    }

    override fun getAlbumGroupedScrobbles(request: GroupedAlbumScrobbleRequest): List<GroupedAlbumResultMapper> {
        val query = entityManager.createNativeQuery(request.buildQuery())
        return query.resultList.map {
            val rowItemList = it as Array<*>
            val count = (rowItemList[0] as BigInteger).toInt()
            val timeGroup = rowItemList[1] as String
            val album = rowItemList[2] as String
            val artist = rowItemList[3] as String
            GroupedAlbumResultMapper(count, timeGroup, album, artist)
        }
    }

    override fun getGroupedScrobbles(request: GroupedScrobbleRequest): List<GroupedResultMapper> {
        val query = entityManager.createNativeQuery(request.buildQuery())
        return query.resultList.map {
            val rowItemList = it as Array<*>
            val count = (rowItemList[0] as BigInteger).toInt()
            GroupedResultMapper(count, rowItemList[1] as String)
        }
    }

    override fun getOldestAndNewestPlay(userName: String, type: String): Array<*> {
        val extraQuery = if (type == "album_name") ", artist_name " else ""

        val query =
            "select $type, max(scrobble.time), min(time), " +
                "max(time)-min(time) as rang " +
                extraQuery +
                "from scrobble " +
                "where user_name = '${userName.toLowerCase()}' " +
                "group by $type $extraQuery" +
                "order by rang desc limit 1"

        return entityManager.createNativeQuery(query).singleResult as Array<*>
    }

    override fun getLongestDormancy(userName: String, type: String): List<*> {
        val extraQuery = if (type == "album_name") ", artist_name " else ""

        val query =
            "select $type, time, " +
                "lag(time) over (partition by $type order by time) as pp, " +
                "time - lag(time) over (partition by $type order by time) as last_play " +
                extraQuery +
                "from scrobble " +
                "where user_name = '${userName.toLowerCase()}' " +
                "order by last_play desc nulls last;"

        return entityManager.createNativeQuery(query).resultList as List<*>
    }

    override fun getScrobbleRunningTotals(request: ScrobbleRunningTotalRequest): List<*> {

        val query =
            "with data as (" +
                "select to_char(to_timestamp(time), 'IYYY') as year," +
                "count(*) " +
                "from scrobble where user_name = '${request.userName}'" +
                "group by to_char(to_timestamp(time), 'IYYY')) " +
                "select year, sum(count) over (order by year " +
                "asc rows between unbounded preceding and current row)" +
                "from data;"

        return entityManager.createNativeQuery(query).resultList as List<*>
    }

    override fun getArtistRank(userName: String, artistName: String): List<*> {
        val query = """
            select artist_name, rank from (
            with counts as (select count(*) as c, artist_name from scrobble 
            where user_name = '$userName' group by artist_name order by c desc)
            select *, RANK() over (order by c desc) from counts) t;
        """.trimIndent()

        return entityManager.createNativeQuery(query).resultList as List<*>
    }

    override fun getTopFivePlays(userName: String, artistName: String): List<*> {
        val query = """
            select count(*), name from scrobble where user_name = '$userName' 
            and lower(artist_name) = '$artistName' group by name order by count(*) desc limit 5;
        """.trimIndent()

        return entityManager.createNativeQuery(query).resultList as List<*>
    }

    override fun getMostRecent(userName: String, artistName: String): List<*> {
        val query = """
            select * from scrobble where user_name = '$userName' 
            and lower(artist_name) = '$artistName' order by time desc limit 1;
        """.trimIndent()

        return entityManager.createNativeQuery(query).resultList as List<*>
    }

    override fun getFirstPlay(userName: String, artistName: String): List<*> {
        val query = """
            select * from scrobble where user_name = '$userName' 
            and lower(artist_name) = '$artistName' order by time asc limit 1;
        """.trimIndent()

        return entityManager.createNativeQuery(query).resultList as List<*>
    }
}
