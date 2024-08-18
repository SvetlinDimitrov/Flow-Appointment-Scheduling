package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import java.util.List;

public class NotFoundException extends RuntimeException {

  private final Exceptions exceptionMessage;

  public NotFoundException(Exceptions exceptionMessage, List<String> values) {
    super(String.format(exceptionMessage.message, values.toArray()));
    this.exceptionMessage = exceptionMessage;
  }

  public NotFoundException(Exceptions exceptionMessage) {
    super(exceptionMessage.message);
    this.exceptionMessage = exceptionMessage;
  }

  public String getTitle() {
    return exceptionMessage.type;
  }
}
