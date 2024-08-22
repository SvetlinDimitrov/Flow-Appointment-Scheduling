package com.internship.flow_appointment_scheduling.features.appointments.annotations.required_appointment_date;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredAppointmentDateValidator implements
    ConstraintValidator<RequiredAppointmentDate, AppointmentUpdate> {

  @Override
  public boolean isValid(AppointmentUpdate dto, ConstraintValidatorContext context) {
    if (dto.status() == AppointmentStatus.NOT_APPROVED ||
        dto.status() == AppointmentStatus.APPROVED) {
      return dto.date() != null;
    }
    return true;
  }
}