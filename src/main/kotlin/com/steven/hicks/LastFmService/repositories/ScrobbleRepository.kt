package com.steven.hicks.LastFmService.repositories

import com.steven.hicks.LastFmService.entities.data.Scrobble
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScrobbleRepository : JpaRepository<Scrobble, Long>