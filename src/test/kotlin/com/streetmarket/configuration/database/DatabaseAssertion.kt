package com.streetmarket.configuration.database

interface DatabaseAssertion {

    fun assertDatabase(
        fromClause: String,
        expected: String,
        vararg ignoredColumns: String
    )

    fun executeQuery(query: String)

    fun executeCount(fromClause: String): Int

    fun executeScript(path: String)
}
