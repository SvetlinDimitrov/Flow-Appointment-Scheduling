package com.internship.flow_appointment_scheduling.features.appointments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentCreate(
    @NotNull(message = "Service id is required")
        @Min(value = 1, message = "Service id must be at least 1")
        Long serviceId,
    @NotBlank(message = "Client email is required") @Email(message = "Client email is invalid")
        String clientEmail,
    @NotBlank(message = "Staff email is required") @Email(message = "Staff email is invalid")
        String staffEmail,
    @NotNull(message = "Date is required")
        @FutureOrPresent(message = "Date must be in the present or future")
        LocalDateTime date) {}
