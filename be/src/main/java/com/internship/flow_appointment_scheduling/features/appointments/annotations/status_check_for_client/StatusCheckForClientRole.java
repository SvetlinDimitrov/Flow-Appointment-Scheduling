package com.internship.flow_appointment_scheduling.features.appointments.annotations.status_check_for_client;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StatusCheckForClientRoleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusCheckForClientRole {
  String message() default "Users with role 'CLIENT' cannot set status to APPROVED or COMPLETED";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
