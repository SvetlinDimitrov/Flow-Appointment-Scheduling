package com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums;

public enum ExceptionMessages {

  USER_NOT_FOUND("User not found with id: %s"),
  USER_NOT_FOUND_BY_EMAIL("User not found with email: %s"),
  USER_ALREADY_EXISTS("User already exists with email: %s"),
  REFRESH_TOKEN_NOT_FOUND("Refresh token %s not found"),
  REFRESH_TOKEN_EXPIRED("Refresh token has expired"),
  SERVICE_NOT_FOUND("Service not found with id: %s"),
  SERVICE_NOT_FOUND_BY_NAME("Service not found with name: %s"),
  USER_NOT_FOUND_IN_SERVICE("User with email: %s not found in service with id: %s"),
  USER_ALREADY_ASSIGN_TO_SERVICE("User with email: %s already assign to service with id: %s"),
  WORK_SPACE_NOT_FOUND("WorkSpace not found with id: %s"),
  WORK_SPACE_NOT_FOUND_BY_NAME("WorkSpace not found with name: %s");

  public final String message;

  ExceptionMessages(String message) {
    this.message = message;
  }

}
