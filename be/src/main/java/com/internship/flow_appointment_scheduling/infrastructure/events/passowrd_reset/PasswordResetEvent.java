package com.internship.flow_appointment_scheduling.infrastructure.events.passowrd_reset;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordResetEvent extends ApplicationEvent {

  private final String email;
  private final String token;

  public PasswordResetEvent(Object source, String email, String token) {
    super(source);
    this.email = email;
    this.token = token;
  }
}
