package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }

  public String getTITLE() {
    return "User not found";
  }
}
