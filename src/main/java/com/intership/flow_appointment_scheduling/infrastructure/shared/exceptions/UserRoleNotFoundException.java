package com.intership.flow_appointment_scheduling.infrastructure.shared.exceptions;

public class UserRoleNotFoundException extends RuntimeException {

    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
