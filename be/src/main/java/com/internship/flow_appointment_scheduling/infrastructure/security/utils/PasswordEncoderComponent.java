package com.internship.flow_appointment_scheduling.infrastructure.security.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderComponent {

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public PasswordEncoderComponent(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public String encode(String password) {
    return passwordEncoder.encode(password);
  }
}
