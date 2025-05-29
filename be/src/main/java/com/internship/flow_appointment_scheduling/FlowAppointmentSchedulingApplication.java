package com.internship.flow_appointment_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlowAppointmentSchedulingApplication {

  public static void main(String[] args) {
    SpringApplication.run(FlowAppointmentSchedulingApplication.class, args);
  }
}
