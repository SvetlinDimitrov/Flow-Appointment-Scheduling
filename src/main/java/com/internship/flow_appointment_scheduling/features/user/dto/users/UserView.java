package com.internship.flow_appointment_scheduling.features.user.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.internship.flow_appointment_scheduling.features.user.dto.employee_details.EmployeeDetailsView;

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
