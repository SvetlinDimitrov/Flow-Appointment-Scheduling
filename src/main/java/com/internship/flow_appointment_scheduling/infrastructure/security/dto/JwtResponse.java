package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

import java.time.LocalDateTime;

public record JwtResponse(String jwt, LocalDateTime expirationTime) {
}
