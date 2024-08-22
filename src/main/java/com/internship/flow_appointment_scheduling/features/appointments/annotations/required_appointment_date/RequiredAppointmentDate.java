package com.internship.flow_appointment_scheduling.features.appointments.annotations.required_appointment_date;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredAppointmentDateValidator.class)
public @interface RequiredAppointmentDate {

  String message() default "Date must be present when status is NOT_APPROVED or APPROVED";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}