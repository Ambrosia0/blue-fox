package com.ambrosia.comment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;

@ImportRuntimeHints(DatabaseHints.class)
@Configuration
public class DatabaseConfig {
    @Bean
    JdbcPostgresDialect jdbcDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }
}
