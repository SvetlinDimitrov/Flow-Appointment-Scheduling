package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(

    @NotBlank(message = "Email must not be blank")
    @Size(min = 3, max = 255, message = "Email must not exceed 255 characters and must be at least 3 characters")
    @Email(message = "Email must be valid")
    String email,

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 255, message = "Password must not exceed 255 characters and must be at least 8 characters")
    String password
) {
}
