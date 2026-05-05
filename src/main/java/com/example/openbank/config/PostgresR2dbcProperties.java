package com.example.openbank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.r2dbc")
public record PostgresR2dbcProperties(
        String url,
        String username,
        String password
) {
}
