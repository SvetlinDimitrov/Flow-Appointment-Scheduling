package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

public record AuthenticationRequest(String email, String password) {
}
