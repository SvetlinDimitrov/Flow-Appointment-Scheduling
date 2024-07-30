package com.intership.flow_appointment_scheduling.feature.user.dto;

public record UserView(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String role
) {
}
