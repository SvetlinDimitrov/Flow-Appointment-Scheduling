package com.internship.flow_appointment_scheduling.web;

import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping
  public JwtResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password())
    );

    return jwtUtil.generateToken(authenticationRequest.email());
  }
}