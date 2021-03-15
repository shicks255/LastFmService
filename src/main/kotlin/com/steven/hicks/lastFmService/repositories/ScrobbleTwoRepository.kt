package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.entities.data.ScrobbleTwo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScrobbleTwoRepository : JpaRepository<ScrobbleTwo, Long>
