package com.internship.flow_appointment_scheduling.infrastructure.exceptions.services;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.GeneralException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;

public class UserNotFoundInTheServiceException extends RuntimeException implements GeneralException {

  public UserNotFoundInTheServiceException(Long serviceId, String userEmail) {
    super(String.format(ExceptionMessages.USER_NOT_FOUND_IN_SERVICE.message, userEmail, serviceId));
  }

  public String getTitle() {
    return "User not found in the service";
  }
}
