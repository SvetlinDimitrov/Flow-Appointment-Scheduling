package com.internship.flow_appointment_scheduling.infrastructure.security.service;

import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenPostRequest;

public interface JwtService {

  AuthenticationResponse refreshToken(RefreshTokenPostRequest dto);

  AuthenticationResponse generateToken(String email);

  Boolean isJwtTokenExpired(String token);

  String getEmailFromToken(String token);
}
