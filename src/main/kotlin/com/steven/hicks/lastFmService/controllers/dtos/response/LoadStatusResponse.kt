package com.steven.hicks.lastFmService.controllers.dtos.response

data class LoadStatusResponse(
    val currentPage: Int,
    val totalPages: Int,
    val message: String,
)
