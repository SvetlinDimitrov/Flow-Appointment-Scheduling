package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

public record AuthenticationResponse(JwtView jwtToken, RefreshTokenView refreshToken) {

}
