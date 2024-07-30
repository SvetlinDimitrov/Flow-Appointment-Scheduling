package com.intership.flow_appointment_scheduling.feature.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPutRequest(

    @Size(max = 255, message = "First name must not exceed 255 characters")
    @NotBlank(message = "Last name must not be blank")
    String firstName,

    @Size(max = 255, message = "Last name must not exceed 255 characters")
    @NotBlank(message = "Last name must not be blank")
    String lastName

) {
}
