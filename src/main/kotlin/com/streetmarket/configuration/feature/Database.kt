package com.streetmarket.configuration.feature

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.config.HoconApplicationConfig
import io.ktor.util.AttributeKey
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class Database private constructor() {

    companion object Feature : ApplicationFeature<Application, Nothing, Database> {

        override val key = AttributeKey<Database>("Database")

        override fun install(
            pipeline: Application,
            configure: Nothing.() -> Unit
        ): Database {
            val feature = Database()

            val hoconConfiguration = HoconApplicationConfig(ConfigFactory.load()).config("ktor.datasource")

            val hikariConfig = HikariConfig().apply {
                poolName = hoconConfiguration.property("name").getString()
                jdbcUrl = hoconConfiguration.property("url").getString()
                username = hoconConfiguration.property("username").getString()
                password = hoconConfiguration.property("password").getString()
                driverClassName = hoconConfiguration.property("className").getString()
                maximumPoolSize = 10
                isAutoCommit = false
            }

            org.jetbrains.exposed.sql.Database.connect(HikariDataSource(hikariConfig))

            transaction {
                SchemaUtils.setSchema(Schema("MARKET"))
            }

            return feature
        }
    }
}
