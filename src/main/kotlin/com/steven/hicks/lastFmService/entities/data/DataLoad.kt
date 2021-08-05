package com.steven.hicks.lastFmService.entities.data

import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class DataLoad(
    @Id
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    var timestamp: OffsetDateTime,
    @Enumerated(EnumType.STRING)
    var status: DataLoadStatus,
    var count: Int,
    @Column(columnDefinition = "text")
    var error: String? = ""
)

enum class DataLoadStatus {
    RUNNING,
    ERROR,
    SUCCESS
}
