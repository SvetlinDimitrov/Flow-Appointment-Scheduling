package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;

public class BadRequestException extends RuntimeException {

  private final Exceptions exceptionMessage;

  public BadRequestException(Exceptions exceptionMessage, String value, Long value2) {
    super(String.format(exceptionMessage.message, value, value2));
    this.exceptionMessage = exceptionMessage;
  }

  public BadRequestException(Exceptions exceptionMessage) {
    super(exceptionMessage.message);
    this.exceptionMessage = exceptionMessage;
  }

  public String getTitle() {
    return exceptionMessage.type;
  }
}

