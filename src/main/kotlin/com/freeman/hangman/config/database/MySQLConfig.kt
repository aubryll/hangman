package com.freeman.hangman.config.database

import com.freeman.hangman.config.properties.DatabaseProperties
import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration
import dev.miku.r2dbc.mysql.MySqlConnectionFactory
import dev.miku.r2dbc.mysql.constant.SslMode
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.proxy.ProxyConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.jetbrains.annotations.NotNull
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import java.time.Duration
import javax.annotation.PreDestroy

@Configuration
class MySQLConfig(
    private val properties: DatabaseProperties
) : AbstractR2dbcConfiguration() {

    lateinit var connectionPool: ConnectionPool

    @NotNull
    @Bean
    @Lazy
    override fun connectionFactory(): ConnectionFactory {
        val connectionFactory: ConnectionFactory = MySqlConnectionFactory.from(
            MySqlConnectionConfiguration.builder()
                .host(properties.host)
                .database(properties.database)
                .port(properties.port.toInt())
                .username(properties.username)
                .password(properties.password)
                .connectTimeout(Duration.ofSeconds(60))
                .sslMode(SslMode.DISABLED)
                .useServerPrepareStatement()
                .build()
        )

        val configuration = ConnectionPoolConfiguration.builder(connectionFactory)
            .maxIdleTime(Duration.ofMillis(1000))
            .maxSize(20)
            .build()

        connectionPool = ConnectionPool(configuration)
        return ProxyConnectionFactory.builder(connectionPool)
            .build()
    }

    @PreDestroy
    fun onDestroy() {
        connectionPool.close()
    }
}