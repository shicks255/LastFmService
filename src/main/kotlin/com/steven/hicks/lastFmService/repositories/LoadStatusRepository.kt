package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.entities.data.LoadStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoadStatusRepository : JpaRepository<LoadStatus, String>