package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;

public class ServiceNotFoundException extends RuntimeException implements GeneralException {

  public ServiceNotFoundException(Long id) {
    super(String.format(ExceptionMessages.SERVICE_NOT_FOUND.message, id));
  }

  public ServiceNotFoundException(String name) {
    super(String.format(ExceptionMessages.SERVICE_NOT_FOUND_BY_NAME.message, name));
  }

  public String getTitle() {
    return "Service not found";
  }
}
