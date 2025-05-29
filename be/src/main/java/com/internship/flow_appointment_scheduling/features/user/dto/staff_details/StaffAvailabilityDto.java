package com.internship.flow_appointment_scheduling.features.user.dto.staff_details;

import com.internship.flow_appointment_scheduling.features.user.annotations.working_hours.ValidWorkingHours;
import jakarta.validation.constraints.NotNull;

@ValidWorkingHours
public record StaffAvailabilityDto(
    @NotNull(message = "Is available must not be null") Boolean isAvailable,
    @NotNull(message = "Begin working hour must not be null") String beginWorkingHour,
    @NotNull(message = "End working hour must not be null") String endWorkingHour) {}
