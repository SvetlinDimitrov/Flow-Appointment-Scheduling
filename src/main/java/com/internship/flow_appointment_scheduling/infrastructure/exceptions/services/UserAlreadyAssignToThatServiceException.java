package com.internship.flow_appointment_scheduling.infrastructure.exceptions.services;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.GeneralException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;

public class UserAlreadyAssignToThatServiceException extends RuntimeException
    implements GeneralException {

  public UserAlreadyAssignToThatServiceException(Long serviceId, String userEmail) {
    super(String.format(ExceptionMessages.USER_ALREADY_ASSIGN_TO_SERVICE.message, userEmail, serviceId));
  }

  public String getTitle() {
    return "User not found in the service";
  }
}
