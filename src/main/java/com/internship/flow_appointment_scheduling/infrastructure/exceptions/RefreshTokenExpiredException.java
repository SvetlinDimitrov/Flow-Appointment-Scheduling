package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;

public class RefreshTokenExpiredException extends RuntimeException implements GeneralException {

  public RefreshTokenExpiredException() {
    super(ExceptionMessages.REFRESH_TOKEN_EXPIRED.message);
  }

  @Override
  public String getTitle() {
    return "Refresh Token Expired";
  }
}
