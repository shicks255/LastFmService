package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.entities.data.Scrobble
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScrobbleRepository : JpaRepository<Scrobble, Long>, CustomScrobbleRepository {
    fun findTopByOrderByTimeDesc(): Scrobble
}
