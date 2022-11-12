package com.steven.hicks.lastFmService.config

import javax.sql.DataSource
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatasourceConfig {

    @Bean
    fun thing(): DataSource {
        val dbUser = System.getenv("dbuser")
        val dbPassword = System.getenv("dbpassword")

        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName("org.postgresql.Driver")
        dataSourceBuilder.url("jdbc:postgresql://shicks255.com:5432/Lastfm")
        dataSourceBuilder.username(dbUser)
        dataSourceBuilder.password(dbPassword)
        return dataSourceBuilder.build()
    }
}
