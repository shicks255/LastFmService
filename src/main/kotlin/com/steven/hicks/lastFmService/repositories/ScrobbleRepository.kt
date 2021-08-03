package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.entities.data.Scrobble
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScrobbleRepository : JpaRepository<Scrobble, Long>, CustomScrobbleRepository {

    //    @Query("select s from scrobble s where s.user_name = ?1 order by s.time desc limit 1", nativeQuery = true)
    fun findTopByUserNameOrderByTimeDesc(userName: String): Scrobble

    fun existsScrobbleByUserNameEquals(userName: String): Boolean
}
