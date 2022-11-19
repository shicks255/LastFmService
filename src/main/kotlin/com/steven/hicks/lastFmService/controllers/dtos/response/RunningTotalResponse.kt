package com.steven.hicks.lastFmService.controllers.dtos.response

data class RunningTotalResponse(
    val data: List<RunningTotals>
)

data class RunningTotals(
    val timeGroup: String,
    val count: Int
)
