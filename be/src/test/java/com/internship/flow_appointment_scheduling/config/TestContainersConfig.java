package com.internship.flow_appointment_scheduling.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySQLContainer() {
        MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.3.0"));
        mysql.start();
        return mysql;
    }
}