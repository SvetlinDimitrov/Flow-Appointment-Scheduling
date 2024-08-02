package com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums;

public enum ExceptionMessages {

  USER_NOT_FOUND("User not found with id: %s"),
  USER_NOT_FOUND_BY_EMAIL("User not found with email: %s"),
  USER_ALREADY_EXISTS("User already exists with email: %s"),
  REFRESH_TOKEN_NOT_FOUND("Refresh token %s not found"),
  REFRESH_TOKEN_EXPIRED("Refresh token has expired");

  public final String message;

  ExceptionMessages(String message) {
    this.message = message;
  }

}
