package com.internship.flow_appointment_scheduling.features.user.annotations.working_hours;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WorkingHoursValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkingHours {
  String message() default "Begin working hour must be before end working hour";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
