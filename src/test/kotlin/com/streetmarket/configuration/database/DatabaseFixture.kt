package com.streetmarket.configuration.database

import org.dbunit.Assertion.assertEqualsByQuery
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.database.QueryDataSet
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlProducer
import org.dbunit.ext.h2.H2DataTypeFactory
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.sql.DriverManager
import java.util.stream.Collectors

class DatabaseFixture : DatabaseAssertion {

    private var databaseConnection: IDatabaseConnection? = null
    private val connection =
        DriverManager.getConnection(
            "jdbc:h2:mem:test;init=runscript from 'classpath:schema.sql'",
            "market",
            "market"
        )

    init {
        databaseConnection = DatabaseConnection(connection)
        databaseConnection?.apply {
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, H2DataTypeFactory())
        }
    }

    override fun assertDatabase(
        fromClause: String,
        expected: String,
        vararg ignoredColumns: String
    ) {
        assertDatabase(
            tableName = fromClause.substringBefore(" "),
            fromClause,
            expected,
            *ignoredColumns
        )
    }

    private fun assertDatabase(
        tableName: String,
        fromClause: String,
        expected: String,
        vararg ignoredColumns: String
    ) {
        assertEqualsByQuery(
            loadDataSet("/expected/datasets/$expected"),
            databaseConnection,
            "SELECT * FROM $fromClause",
            tableName,
            ignoredColumns
        )
    }

    override fun executeQuery(query: String) {
        connection
            .prepareStatement(query)
            .executeUpdate()
    }

    override fun executeCount(fromClause: String): Int {
        val resultSet = connection
            .prepareStatement("SELECT COUNT(*) FROM $fromClause")
            .executeQuery()

        resultSet.next()
        val count = resultSet.getInt(1)
        resultSet.close()

        return count
    }

    override fun executeScript(path: String) {
        val script = BufferedReader(InputStreamReader(object {}.javaClass.getResourceAsStream("/$path")))
            .lines().collect(Collectors.joining("\n"))

        connection.prepareStatement(script).executeUpdate()
    }

    private fun loadDataSet(datasetFile: String) = run {
        val source = InputSource(javaClass.getResourceAsStream(datasetFile))
        val producer = FlatXmlProducer(source, false, true)

        ReplacementDataSet(FlatXmlDataSet(producer)).apply {
            addReplacementObject("[NULL]", null)
        }
    }

    fun databaseExpectationBuilder(): DatabaseExpectationBuilder = DatabaseExpectationBuilder()

    inner class DatabaseExpectationBuilder(
        private var mappedQueries: MutableMap<String, MutableList<String>> = mutableMapOf()
    ) {

        fun addExpectation(tableName: String): DatabaseExpectationBuilder =
            addExpectation(tableName, "SELECT * FROM $tableName")

        private fun addExpectation(tableName: String, query: String): DatabaseExpectationBuilder {
            if (mappedQueries.containsKey(tableName)) {
                mappedQueries[tableName]?.add(query)
            } else {
                mappedQueries[tableName] = mutableListOf(query)
            }
            return this
        }

        @Throws(Exception::class)
        fun write(fileName: String?) {
            val queryDataSet = QueryDataSet(databaseConnection)

            mappedQueries.forEach { (key, value) ->
                value.forEach { query -> queryDataSet.addTable(key, query) }
            }

            FlatXmlDataSet.write(queryDataSet, FileOutputStream("src/test/resources/expected/datasets/$fileName"))
        }
    }
}
