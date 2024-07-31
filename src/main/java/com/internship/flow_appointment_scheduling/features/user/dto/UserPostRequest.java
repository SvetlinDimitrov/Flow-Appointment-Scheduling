package com.internship.flow_appointment_scheduling.features.user.dto;

import com.internship.flow_appointment_scheduling.features.user.annotations.email.UniqueEmail;
import com.internship.flow_appointment_scheduling.features.user.annotations.password.ValidPassword;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserPostRequest(

    @ValidPassword
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 255, message = "Password must not exceed 255 characters and must be at least 8 characters")
    String password,

    @NotBlank(message = "Email must not be blank")
    @Size(min = 3, max = 255, message = "Email must not exceed 255 characters and must be at least 3 characters")
    @Email(message = "Email must be valid")
    @UniqueEmail()
    String email,

    @Size(min = 3, max = 255, message = "First name must not exceed 255 characters and must be at least 3 characters")
    String firstName,

    @Size(min = 3, max = 255, message = "Last name must not exceed 255 characters and must be at least 3 characters")
    String lastName,

    @NotNull(message = "Role must not be blank")
    UserRoles role

) {
}
