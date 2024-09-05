package com.internship.flow_appointment_scheduling.infrastructure.events.appointments;

import com.internship.flow_appointment_scheduling.infrastructure.mail_service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentNotificationEventListener {

  private final MailService mailService;

  @EventListener
  public void handleAppointmentNotificationEvent(AppointmentNotificationEvent event) {
    switch (event.getNotificationType()) {
      case APPROVED -> mailService.sendApprovedAppointmentNotification(event.getAppointment());
      case NOT_APPROVED ->
          mailService.sendNotApprovedAppointmentNotification(event.getAppointment());
      case CANCELED ->
          mailService.sendCanceledAppointmentNotificationToClient(event.getAppointment());
    }
  }
}