package com.internship.flow_appointment_scheduling.features.user.dto.employee_details;

import com.internship.flow_appointment_scheduling.features.user.annotations.working_hours.ValidWorkingHours;
import jakarta.validation.constraints.NotNull;

@ValidWorkingHours
public record EmployeeWorkingHours(

    @NotNull(message = "Begin working hour must not be null")
    String beginWorkingHour,

    @NotNull(message = "End working hour must not be null")
    String endWorkingHour
) {

}
