package com.steven.hicks.lastFmService.entities.queryBuilding

enum class WhereOperator(val field: String) {
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    EQ("="),
    NE("<>"),
    IN("in")
}