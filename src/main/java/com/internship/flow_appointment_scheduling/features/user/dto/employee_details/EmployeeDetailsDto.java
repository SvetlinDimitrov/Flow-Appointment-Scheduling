package com.internship.flow_appointment_scheduling.features.user.dto.employee_details;

import com.internship.flow_appointment_scheduling.features.user.annotations.working_hours.ValidWorkingHours;
import com.internship.flow_appointment_scheduling.features.user.annotations.working_hours.WorkingHours;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@ValidWorkingHours
public record EmployeeDetailsDto(

    @NotNull(message = "Salary must not be null")
    @Min(value = 0, message = "Salary must be at least 0")
    Double salary,

    @NotNull(message = "Begin working hour must not be null")
    LocalTime beginWorkingHour,

    @NotNull(message = "End working hour must not be null")
    LocalTime endWorkingHour
) implements WorkingHours {

    @Override
    public LocalTime getBeginWorkingHour() {
        return beginWorkingHour;
    }

    @Override
    public LocalTime getEndWorkingHour() {
        return endWorkingHour;
    }
}