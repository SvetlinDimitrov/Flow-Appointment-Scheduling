package com.internship.flow_appointment_scheduling.features.user.dto.staff_details;

import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPostRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record StaffHireDto(
    @NotNull(message = "User info must not be null") @Valid UserPostRequest userInfo,
    @NotNull(message = "Staff details must not be null") @Valid StaffDetailsDto staffDetailsDto) {}
