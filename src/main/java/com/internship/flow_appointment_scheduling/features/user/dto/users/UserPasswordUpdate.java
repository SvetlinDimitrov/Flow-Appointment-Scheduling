package com.internship.flow_appointment_scheduling.features.user.dto.users;

import com.internship.flow_appointment_scheduling.features.user.annotations.password.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdate(

    @ValidPassword
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 255, message = "Password must not exceed 255 characters and must be at least 8 characters")
    String newPassword

) {

}
