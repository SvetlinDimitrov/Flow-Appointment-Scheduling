package com.internship.flow_appointment_scheduling.features.user.annotations.adminOnly;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AdminOnlyValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminOnly {
    String message() default "Only administrators can set this field";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
