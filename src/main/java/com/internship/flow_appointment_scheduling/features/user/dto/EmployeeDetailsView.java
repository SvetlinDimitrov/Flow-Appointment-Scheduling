package com.internship.flow_appointment_scheduling.features.user.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

public record EmployeeDetailsView(
    BigDecimal salary,
    BigDecimal profit,
    Integer completedAppointments,
    Double experience,
    LocalTime beginWorkingHour,
    LocalTime endWorkingHour
) {

}
