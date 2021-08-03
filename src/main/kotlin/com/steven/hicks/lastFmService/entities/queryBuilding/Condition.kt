package com.steven.hicks.lastFmService.entities.queryBuilding

import com.steven.hicks.lastFmService.entities.ScrobbleField

data class Condition(
    val scrobbleField: ScrobbleField,
    val op: WhereOperator,
    val item: Any
)
