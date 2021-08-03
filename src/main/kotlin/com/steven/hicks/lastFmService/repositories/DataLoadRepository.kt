package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.entities.data.DataLoad
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DataLoadRepository : JpaRepository<DataLoad, LocalDate>
