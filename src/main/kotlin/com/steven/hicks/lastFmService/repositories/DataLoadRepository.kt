package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.entities.data.DataLoad
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DataLoadRepository : JpaRepository<DataLoad, LocalDate> {

    @Query(
        "select * from data_Load d where d.user_name = ?1 and d.status = 'SUCCESS' order by d.timestamp desc limit 1",
        nativeQuery = true
    )
    fun getLastSuccessfull(userName: String): DataLoad?
}
