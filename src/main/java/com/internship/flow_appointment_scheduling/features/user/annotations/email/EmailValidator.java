package com.internship.flow_appointment_scheduling.features.user.annotations.email;

import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator implements ConstraintValidator<UniqueEmail, String> {

  private final UserRepository userRepository;

  public EmailValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    if (email == null) {
      return false;
    }

    if (userRepository.existsByEmail(email)) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(String.format(ExceptionMessages.USER_ALREADY_EXISTS.message, email))
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}