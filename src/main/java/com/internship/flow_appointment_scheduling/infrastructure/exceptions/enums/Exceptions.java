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
  USER_IS_NOT_AN_STAFF(
      "User is not an staff",
      "User is not an staff"
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
  ),
  APPOINTMENT_NOT_FOUND(
      "Appointment not found with id: %s",
      "Appointment not found"
  ),
  APPOINTMENT_WRONG_CLIENT_ROLE(
      "User with email: %s is not an client",
      "User is not an client"
  ),
  APPOINTMENT_WRONG_STAFF_ROLE(
      "User with email: %s is not an staff",
      "User is not an staff"
  ),
  APPOINTMENT_OVERLAP(
      "Appointment overlap with another appointment",
      "Appointment overlap"
  ),
  APPOINTMENT_STAFF_NOT_AVAILABLE(
      "Staff with email: %s is not available at the given time",
      "Staff not available"
  ),
  APPOINTMENT_SERVICE_NOT_AVAILABLE(
      "Service with id: %s is not available at the given time",
      "Service not available"
  ),
  APPOINTMENT_STAFF_NOT_CONTAINING_SERVICE(
      "Staff with email: %s is not containing service with id: %s",
      "Staff not containing service"
  ),
  APPOINTMENT_WORK_SPACE_NOT_AVAILABLE(
      "WorkSpace with id: %s is not available at the given time",
      "WorkSpace not available"
  ),
  APPOINTMENT_CANNOT_BE_MODIFIED(
      "Completed or canceled appointment cannot be modified.",
      "Appointment cannot be modified"
  ),
  APPOINTMENT_NOT_APPROVED(
      "Appointment cannot be set to not approved.",
      "Appointment cannot be set to not approved"
  ),
  APPOINTMENT_ALREADY_IS_APPROVED(
      "Appointment is already approved.",
      "Appointment is already approved"
  ),;

  public final String message;
  public final String type;

  Exceptions(String message, String type) {
    this.message = message;
    this.type = type;
  }

}
