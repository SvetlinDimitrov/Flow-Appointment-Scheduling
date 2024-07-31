package com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums;

public enum ExceptionMessages {

  USER_NOT_FOUND("User not found with id: %s"),
  USER_ALREADY_EXISTS("User already exists with email: %s");

  public final String message;

  ExceptionMessages(String message) {
    this.message = message;
  }

}
