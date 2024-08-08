package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

import jakarta.validation.constraints.Email;

public record AuthenticationRequest(

    @Email(message = "Email must be valid")
    String email,

    String password
) {

}
