package com.intership.flow_appointment_scheduling.infrastructure.shared.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }

}
