package com.internship.flow_appointment_scheduling.infrastructure.exceptions.auth;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.GeneralException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;

public class RefreshTokenNotFoundException extends RuntimeException implements GeneralException {

  public RefreshTokenNotFoundException(String token) {
    super(String.format(ExceptionMessages.REFRESH_TOKEN_NOT_FOUND.message, token));
  }

  public String getTitle() {
    return "Refresh token not found";
  }
}
