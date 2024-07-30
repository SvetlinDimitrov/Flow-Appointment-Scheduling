package com.intership.flow_appointment_scheduling.feature.user.dto;

import com.intership.flow_appointment_scheduling.feature.user.entity.enums.UserRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserPostRequest(

    @NotBlank(message = "Password must not be blank")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    String password,

    @NotBlank(message = "Email must not be blank")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Email(message = "Email must be valid")
    String email,

    @Size(max = 255, message = "First name must not exceed 255 characters")
    String firstName,

    @Size(max = 255, message = "Last name must not exceed 255 characters")
    String lastName,

    @NotNull(message = "Role must not be blank")
    UserRoles role

) {
}
