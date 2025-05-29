package com.internship.flow_appointment_scheduling.infrastructure.events.passowrd_reset;

import com.internship.flow_appointment_scheduling.infrastructure.mail_service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetEventListener {

  private final MailService mailService;

  @EventListener
  public void handlePasswordResetEvent(PasswordResetEvent event) {
    mailService.sendResetPasswordEmail(event.getToken(), event.getEmail());
  }
}
