package com.internship.flow_appointment_scheduling.features.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record EmployeeDetailsDto(

    @NotNull(message = "Salary must not be null")
    @Min(value = 0, message = "Salary must be at least 0")
    Double salary,

    @NotNull(message = "Experience must not be null")
    @Min(value = 0, message = "Salary must be at least 0")
    Double experience,

    @NotNull(message = "Begin working hour must not be null")
    LocalTime beginWorkingHour,

    @NotNull(message = "End working hour must not be null")
    LocalTime endWorkingHour
) {

}