package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(Long id) {
    super(String.format(ExceptionMessages.USER_NOT_FOUND.message, id));
  }

  public String getTitle() {
    return "User not found";
  }
}
