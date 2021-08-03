package com.steven.hicks.lastFmService.entities.queryBuilding

import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.Table
import com.steven.hicks.lastFmService.repositories.CustomScrobbleRepositoryImpl
import java.time.LocalDate
import java.time.ZoneOffset

class QueryBuilder {

    companion object {
        fun build(init: QueryBuilder.() -> Unit): String {

            val q = QueryBuilder()
            q.init()
            return q.query
        }
    }

    private var query: String = ""

    fun select(scrobbleField: ScrobbleField, fields: SelectBuilder.() -> Unit) {
        val sb = SelectBuilder(scrobbleField)
        sb.fields()
        query += sb
    }

    fun from(table: Table) {
        query += " from $table"
    }

    fun where(condition: Condition, init: WhereBuilder.() -> Unit) {
        val wb = WhereBuilder(condition)
        wb.init()
        query += wb
    }

    fun groupBy(scrobbleFields: List<ScrobbleField>) {
        query += " group by " + scrobbleFields.joinToString(separator = ",") { it.field }
    }

    fun sort(scrobbleField: ScrobbleField?) {
        if (scrobbleField != null) {
            query += " order by $scrobbleField"
        }
    }

    fun order(direction: Direction?) {
        if (direction != null) {
            query += " ${direction.field}"
        }
    }

    fun limit(limit: Int?) {
        if (limit != null) {
            query += " limit $limit"
        }
    }
}

class SelectBuilder(private val initial: ScrobbleField) {
    private var str = ""

    fun and(anotherScrobbleField: ScrobbleField) {
        str += ", " + anotherScrobbleField.field
    }

    override fun toString(): String {
        return "select ${initial.field}" + str
    }
}

class WhereBuilder(condition: Condition) {
    private val conditions: MutableList<Condition> = mutableListOf(condition)

    fun and(condition: Condition) {
        conditions.add(condition)
    }

    fun andTimeWhere(from: LocalDate?, to: LocalDate?) {
        if (from != null) {
            val fromm = from.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(CustomScrobbleRepositoryImpl.UTC_OFFSET))
            conditions.add(Condition(ScrobbleField.TIME, WhereOperator.GTE, "$fromm"))
        }

        if (to != null) {
            val too = to.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(CustomScrobbleRepositoryImpl.UTC_OFFSET))
            conditions.add(Condition(ScrobbleField.TIME, WhereOperator.LT, "$too"))
        }
    }

    override fun toString(): String {
        return conditions.joinToString(separator = " and ", prefix = " where ") {
            "${it.scrobbleField.field} ${it.op.field} ${it.item}"
        }
    }
}
