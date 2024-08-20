package com.internship.flow_appointment_scheduling.features.user.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EmployeeDetailsView(
    BigDecimal salary,
    BigDecimal profit,
    Integer completedAppointments,
    LocalDate startDate,
    LocalTime beginWorkingHour,
    LocalTime endWorkingHour
) {

}
