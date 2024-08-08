package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenPostRequest(

    @NotBlank(message = "Refresh token is mandatory")
    String token
) {

}
