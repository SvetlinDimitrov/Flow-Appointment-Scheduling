package com.internship.flow_appointment_scheduling.features.appointments.dto;

import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import java.time.LocalDateTime;

public record ShortAppointmentView(
    Long id,
    String serviceName,
    LocalDateTime startDate,
    LocalDateTime endDate,
    AppointmentStatus status
) {

}
