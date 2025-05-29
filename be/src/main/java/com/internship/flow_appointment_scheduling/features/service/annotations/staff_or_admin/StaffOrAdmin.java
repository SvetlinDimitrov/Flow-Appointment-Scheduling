package com.internship.flow_appointment_scheduling.features.service.annotations.staff_or_admin;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StaffOrAdminValidator.class)
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface StaffOrAdmin {
    String message() default "User must have role EMPLOYEE or ADMINISTRATOR";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}