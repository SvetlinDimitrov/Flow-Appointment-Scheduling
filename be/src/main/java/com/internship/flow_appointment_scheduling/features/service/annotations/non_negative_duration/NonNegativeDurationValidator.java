package com.internship.flow_appointment_scheduling.features.service.annotations.non_negative_duration;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Duration;

public class NonNegativeDurationValidator implements
    ConstraintValidator<NonNegativeDuration, Duration> {

  @Override
  public boolean isValid(Duration duration, ConstraintValidatorContext context) {
    return duration != null && !duration.isNegative();
  }
}