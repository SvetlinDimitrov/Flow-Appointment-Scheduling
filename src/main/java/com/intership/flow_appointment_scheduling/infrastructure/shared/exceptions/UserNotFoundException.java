package com.intership.flow_appointment_scheduling.infrastructure.shared.exceptions;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }
}
