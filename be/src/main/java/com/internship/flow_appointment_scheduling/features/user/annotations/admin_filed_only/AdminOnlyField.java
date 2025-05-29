package com.internship.flow_appointment_scheduling.features.user.annotations.admin_filed_only;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdminOnlyFieldValidator.class)
public @interface AdminOnlyField {
  String message() default "User must be an administrator to modify this field";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
