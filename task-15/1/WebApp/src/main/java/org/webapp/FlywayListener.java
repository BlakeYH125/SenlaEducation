package org.webapp;

import org.flywaydb.core.Flyway;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FlywayListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String url = "jdbc:postgresql://localhost:5432/webapp_db";
        String user = "postgres";
        String password = "15492815";
        try {
            Flyway flyway = Flyway.configure().dataSource(url, user, password).locations("classpath:db/migration").load();
            flyway.migrate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}