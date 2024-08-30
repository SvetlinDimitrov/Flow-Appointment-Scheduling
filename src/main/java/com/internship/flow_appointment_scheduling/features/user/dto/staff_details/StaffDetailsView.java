package com.internship.flow_appointment_scheduling.features.user.dto.staff_details;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record StaffDetailsView(
    BigDecimal salary,
    BigDecimal profit,
    Integer completedAppointments,
    Boolean isAvailable,
    LocalDate startDate,
    LocalTime beginWorkingHour,
    LocalTime endWorkingHour,
    List<Long> servicesIds
) {

}
