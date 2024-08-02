package com.internship.flow_appointment_scheduling.infrastructure.security.service;

import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenPostRequest;

public interface JwtService {
  JwtResponse refreshToken(RefreshTokenPostRequest dto);

  JwtResponse generateToken(String email);

  Boolean isJwtTokenExpired(String token);

  String getEmailFromToken(String token);
}
