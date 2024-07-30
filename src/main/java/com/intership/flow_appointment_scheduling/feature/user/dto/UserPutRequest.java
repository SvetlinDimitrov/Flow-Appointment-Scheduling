package com.intership.flow_appointment_scheduling.feature.user.dto;

import com.intership.flow_appointment_scheduling.feature.user.entity.enums.UserRoles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserPutRequest(

    @Size(min = 3, max = 255, message = "First name must not exceed 255 characters")
    @NotBlank(message = "Last name must not be blank")
    String firstName,

    @Size(min = 3, max = 255, message = "Last name must not exceed 255 characters")
    @NotBlank(message = "Last name must not be blank")
    String lastName,

    @NotNull(message = "Role must not be null")
    UserRoles role

) {
}
