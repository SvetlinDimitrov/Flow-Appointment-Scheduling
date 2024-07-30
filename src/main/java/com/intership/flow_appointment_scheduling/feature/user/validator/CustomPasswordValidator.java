package com.intership.flow_appointment_scheduling.feature.user.validator;

import com.intership.flow_appointment_scheduling.infrastructure.shared.exceptions.CreationException;
import org.passay.*;

import java.util.Arrays;
import java.util.List;

public class CustomPasswordValidator {

  public static void validatePassword(String password) {
    PasswordValidator validator = new PasswordValidator(Arrays.asList(
        new LengthRule(8, 30),
        new CharacterRule(EnglishCharacterData.UpperCase, 1),
        new CharacterRule(EnglishCharacterData.LowerCase, 1),
        new CharacterRule(EnglishCharacterData.Digit, 1),
        new CharacterRule(EnglishCharacterData.Special, 1),
        new WhitespaceRule()
    ));

    RuleResult result = validator.validate(new PasswordData(password));

    if (!result.isValid()) {
      List<String> messages = validator.getMessages(result);
      String message = String.join(", ", messages);
      throw new CreationException("Invalid password: " + message);
    }
  }
}
