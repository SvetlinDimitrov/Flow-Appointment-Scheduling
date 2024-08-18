package com.internship.flow_appointment_scheduling.features.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserView(
    Long id,
    String firstName,
    String lastName,
    String email,
    String role,
    EmployeeDetailsView employeeDetails
) {

}
