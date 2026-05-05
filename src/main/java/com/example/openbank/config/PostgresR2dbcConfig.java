package com.example.openbank.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

@Configuration
@EnableConfigurationProperties(PostgresR2dbcProperties.class)
public class PostgresR2dbcConfig extends AbstractR2dbcConfiguration {

    private final PostgresR2dbcProperties properties;

    public PostgresR2dbcConfig(PostgresR2dbcProperties properties) {
        this.properties = properties;
    }

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(properties.url())
                .mutate()
                .option(ConnectionFactoryOptions.USER, properties.username())
                .option(ConnectionFactoryOptions.PASSWORD, properties.password())
                .build();

        return ConnectionFactories.get(options);
    }
}
