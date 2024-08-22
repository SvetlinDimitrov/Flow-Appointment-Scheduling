package com.internship.flow_appointment_scheduling.features.appointment.dto;

import com.internship.flow_appointment_scheduling.features.appointment.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserView;
import java.time.LocalDateTime;

public record AppointmentView(
    Long id,
    UserView client,
    UserView staff,
    LocalDateTime date,
    AppointmentStatus status,
    ServiceView service
) {

}
