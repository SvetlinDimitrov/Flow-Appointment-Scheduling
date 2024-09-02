package com.internship.flow_appointment_scheduling.features.service.annotations.non_negative_duration;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NonNegativeDurationValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonNegativeDuration {
    String message() default "Duration must be non-negative";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}