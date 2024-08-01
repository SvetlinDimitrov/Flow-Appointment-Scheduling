package com.internship.flow_appointment_scheduling.features.user.dto;

public record UserView(
    Long id,
    String firstName,
    String lastName,
    String email,
    String role
) {
}
