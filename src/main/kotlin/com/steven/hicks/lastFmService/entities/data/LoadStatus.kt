package com.steven.hicks.lastFmService.entities.data

import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class LoadStatus(
    @Id()
    val userName: String,

    val totalPages: Int,
    var currentPage: Int,

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    var timestamp: OffsetDateTime,
)
