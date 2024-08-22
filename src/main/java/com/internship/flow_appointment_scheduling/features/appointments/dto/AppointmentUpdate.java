package com.internship.flow_appointment_scheduling.features.appointments.dto;

import com.internship.flow_appointment_scheduling.features.appointments.annotations.required_appointment_date.RequiredAppointmentDate;
import com.internship.flow_appointment_scheduling.features.appointments.annotations.status_check_for_client.StatusCheckForClientRole;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RequiredAppointmentDate
public record AppointmentUpdate(

    @NotNull(message = "Status is required")
    @StatusCheckForClientRole
    AppointmentStatus status,

    @FutureOrPresent(message = "Date must be in the present or future")
    LocalDateTime date
) {

}
