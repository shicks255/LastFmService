package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.entities.data.Scrobble
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ScrobbleRepository : JpaRepository<Scrobble, Long>, CustomScrobbleRepository {

    //    @Query("select s from scrobble s where s.user_name = ?1 order by s.time desc limit 1", nativeQuery = true)
    fun findTopByUserNameOrderByTimeDesc(userName: String): Scrobble

    @Query("select count(*) from scrobble s where s.user_name = ?1 and lower(s.artist_name) = ?2", nativeQuery = true)
    fun countAllByUserNameAndArtistName(userName: String, artistName: String): Int

    @Query("select distinct s.user_name from scrobble s", nativeQuery = true)
    fun findDistinctByUserName(): List<String>
    fun existsScrobbleByUserNameEquals(userName: String): Boolean
}
