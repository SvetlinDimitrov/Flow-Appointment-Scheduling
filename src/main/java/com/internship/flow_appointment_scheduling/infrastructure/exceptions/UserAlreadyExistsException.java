package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }

    public String getTitle() {
        return "User Already Exists";
    }
}
