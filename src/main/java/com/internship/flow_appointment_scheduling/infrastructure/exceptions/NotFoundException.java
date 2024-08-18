package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;

public class NotFoundException extends RuntimeException {

  private final Exceptions exceptionMessage;

  public NotFoundException(Exceptions exceptionMessage, Long id) {
    super(String.format(exceptionMessage.message, id));
    this.exceptionMessage = exceptionMessage;
  }

  public NotFoundException(Exceptions exceptionMessage, String value) {
    super(String.format(exceptionMessage.message, value));
    this.exceptionMessage = exceptionMessage;
  }

  public String getTitle() {
    return exceptionMessage.type;
  }
}
