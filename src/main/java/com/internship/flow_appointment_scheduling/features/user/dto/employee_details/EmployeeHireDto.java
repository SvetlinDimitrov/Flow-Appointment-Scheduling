package com.internship.flow_appointment_scheduling.features.user.dto.employee_details;

import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeDetailsDto;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record EmployeeHireDto(

    @NotNull(message = "User info must not be null")
    @Valid
    UserPostRequest userInfo,

    @NotNull(message = "Employee details must not be null")
    @Valid
    EmployeeDetailsDto employeeDetailsDto) {

}
