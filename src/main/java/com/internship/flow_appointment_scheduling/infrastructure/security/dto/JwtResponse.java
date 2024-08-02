package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

public record JwtResponse(JwtView jwtToken, RefreshTokenView refreshToken) {
}
