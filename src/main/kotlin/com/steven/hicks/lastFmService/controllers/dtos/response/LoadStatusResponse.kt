package com.steven.hicks.lastFmService.controllers.dtos.response

import java.time.OffsetDateTime

data class LoadStatusResponse(
    val currentPage: Int,
    val totalPages: Int,
    val message: String,
    val timestamp: OffsetDateTime
)
