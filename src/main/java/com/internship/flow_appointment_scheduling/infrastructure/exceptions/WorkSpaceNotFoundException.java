package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;

public class WorkSpaceNotFoundException extends RuntimeException implements GeneralException {

  public WorkSpaceNotFoundException(Long id) {
    super(String.format(ExceptionMessages.WORK_SPACE_NOT_FOUND.message, id));
  }

  public WorkSpaceNotFoundException(String name) {
    super(String.format(ExceptionMessages.WORK_SPACE_NOT_FOUND_BY_NAME.message, name));
  }

  public String getTitle() {
    return "WorkSpace not found";
  }
}
