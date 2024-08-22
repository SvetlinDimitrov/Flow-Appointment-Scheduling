package com.internship.flow_appointment_scheduling.features.appointments.dto;

import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentUpdate(

    @NotNull(message = "Status is required")
    AppointmentStatus status,

    @FutureOrPresent(message = "Date must be in the present or future")
    LocalDateTime date
) {

}
