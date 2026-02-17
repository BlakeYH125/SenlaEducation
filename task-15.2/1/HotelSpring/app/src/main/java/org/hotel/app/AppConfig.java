package org.hotel.app;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration(proxyBeanMethods = false)
@ComponentScan("org.hotel")
@PropertySource("settings.properties")
public final class AppConfig {
    /**
     * Ссылка на базу данных.
     */
    @Value("${database.url}")
    private String url;

    /**
     * Имя пользователя базы данных.
     */
    @Value("${database.user}")
    private String user;

    /**
     * Пароль к базе данных.
     */
    @Value("${database.password}")
    private String password;

    private AppConfig() { }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }
}
