package com.internship.flow_appointment_scheduling.features.appointments.dto;

import com.internship.flow_appointment_scheduling.features.appointments.annotations.status_check_for_client.StatusCheckForClientRole;
import com.internship.flow_appointment_scheduling.features.appointments.dto.enums.UpdateAppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentUpdate(
    @NotNull(message = "Status is required") @StatusCheckForClientRole
        UpdateAppointmentStatus status) {}
