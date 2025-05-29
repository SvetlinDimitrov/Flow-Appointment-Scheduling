package com.internship.flow_appointment_scheduling.features.user.annotations.not_client_role;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotClientRoleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotClientRole {
  String message() default "Role must not be CLIENT";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
