package com.internship.flow_appointment_scheduling;

import com.internship.flow_appointment_scheduling.features.appointments.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ContextConfiguration(classes = {TestContainersConfig.class,
    FlowAppointmentSchedulingApplication.class})
class FlowAppointmentSchedulingApplicationTest {

  @Autowired
  private MySQLContainer<?> mySQLContainer;

  @Test
  void contextLoads() {
    System.out.println("MySQL Container is running: " + mySQLContainer.isRunning());
  }
}