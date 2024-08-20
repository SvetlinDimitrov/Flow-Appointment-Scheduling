package com.internship.flow_appointment_scheduling.features.user.dto.staff_details;

import com.internship.flow_appointment_scheduling.features.user.annotations.admin_filed_only.AdminOnlyField;
import com.internship.flow_appointment_scheduling.features.user.annotations.not_client_role.NotClientRole;
import com.internship.flow_appointment_scheduling.features.user.annotations.working_hours.ValidWorkingHours;
import com.internship.flow_appointment_scheduling.features.user.annotations.working_hours.WorkingHours;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@ValidWorkingHours
public record StaffModifyDto(

    @AdminOnlyField
    @NotClientRole
    UserRoles userRole,

    @AdminOnlyField
    @Min(value = 0, message = "Salary must be at least 0")
    Double salary,

    @NotNull(message = "Available must not be null")
    Boolean isAvailable,

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
