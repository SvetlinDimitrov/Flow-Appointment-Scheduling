package com.internship.flow_appointment_scheduling.features.appointments.dto;

import java.time.LocalDateTime;

public record ShortAppointmentView(
    Long id,
    LocalDateTime startDate,
    LocalDateTime endDate
) {

}
