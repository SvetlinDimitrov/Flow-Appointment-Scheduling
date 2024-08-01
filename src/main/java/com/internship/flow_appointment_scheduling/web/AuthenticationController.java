package com.internship.flow_appointment_scheduling.web;

import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/authenticate")
  public JwtResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password())
    );

    UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email());

    return jwtUtil.generateToken(userDetails.getUsername());
  }
}