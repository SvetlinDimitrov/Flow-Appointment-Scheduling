package com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums;

public enum Exceptions {

  USER_NOT_FOUND(
      "User not found with id: %s",
      "User not found"
  ),
  USER_NOT_FOUND_BY_EMAIL(
      "User not found with email: %s",
      "User not found"
  ),
  USER_ALREADY_EXISTS(
      "User already exists with email: %s",
      "User already exists"
  ),
  USER_IS_NOT_AN_EMPLOYEE(
      "User is not an employee",
      "User is not an employee"
  ),
  REFRESH_TOKEN_NOT_FOUND(
      "Refresh token %s not found",
      "Refresh token not found"
  ),
  REFRESH_TOKEN_EXPIRED(
      "Refresh token has expired",
      "Refresh token expired"
  ),
  SERVICE_NOT_FOUND(
      "Service not found with id: %s",
      "Service not found"
  ),
  SERVICE_NOT_FOUND_BY_NAME(
      "Service not found with name: %s",
      "Service not found"
  ),
  USER_NOT_FOUND_IN_SERVICE(
      "User with email: %s not found in service with id: %s",
      "User not found in service"
  ),
  USER_ALREADY_ASSIGN_TO_SERVICE(
      "User with email: %s already assign to service with id: %s",
      "User already assign to service"
  ),
  WORK_SPACE_NOT_FOUND(
      "WorkSpace not found with id: %s",
      "WorkSpace not found"
  ),
  WORK_SPACE_NOT_FOUND_BY_NAME(
      "WorkSpace not found with name: %s",
      "WorkSpace not found"
  );

  public final String message;
  public final String type;

  Exceptions(String message, String type) {
    this.message = message;
    this.type = type;
  }

}
