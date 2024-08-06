package com.internship.flow_appointment_scheduling.infrastructure.security.web;

import com.internship.flow_appointment_scheduling.infrastructure.openapi.AuthenticationControllerDocumentation;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenPostRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController implements AuthenticationControllerDocumentation {

  private final AuthenticationManager authenticationManager;
  private final JwtServiceImpl jwtService;

  public AuthenticationController(AuthenticationManager authenticationManager, JwtServiceImpl refreshTokenService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = refreshTokenService;
  }

  @PostMapping
  public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password())
    );

    return ResponseEntity
        .ok()
        .body(jwtService.generateToken(authenticationRequest.email()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenPostRequest dto) {
    return ResponseEntity
        .ok()
        .body(jwtService.refreshToken(dto));
  }
}