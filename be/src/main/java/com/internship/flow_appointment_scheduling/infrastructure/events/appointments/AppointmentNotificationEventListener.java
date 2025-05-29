package com.internship.flow_appointment_scheduling.infrastructure.events.appointments;

import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.service.AppointmentService;
import com.internship.flow_appointment_scheduling.infrastructure.mail_service.MailService;
import com.internship.flow_appointment_scheduling.infrastructure.scheduler.SchedulerService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentNotificationEventListener {

  private final MailService mailService;
  private final SchedulerService schedulerService;
  private final AppointmentService appointmentService;

  /**
   * Event listener for handling appointment notification events.
   * <p>
   * This method listens for `AppointmentNotificationEvent` and schedules tasks based on the notification type.
   * <ul>
   *   <li>When an appointment is approved, it schedules a task to be executed at the end date. If the appointment status is still approved at the end date, it will be updated to completed automatically. It also sends an approved appointment notification.</li>
   *   <li>When an appointment is not approved, it schedules a task to be executed at the end date. If the appointment status is still not approved at the end date, it will be set to canceled automatically. It also sends a not approved appointment notification.</li>
   *   <li>When an appointment is canceled, it sends a cancellation notification to the client immediately.</li>
   * </ul>
   *
   * @param event the appointment notification event
   */
  @EventListener
  public void handleAppointmentNotificationEvent(AppointmentNotificationEvent event) {
    switch (event.getNotificationType()) {
      case APPROVED -> {
        LocalDateTime endDateToTriggerTheScheduler = event.getAppointment().getEndDate();
        schedulerService.scheduleTask(
            () -> {
              if (event.getAppointment().getStatus() == AppointmentStatus.APPROVED) {
                appointmentService.completeAppointment(event.getAppointment().getId());
              }
            },
            endDateToTriggerTheScheduler
        );
        mailService.sendApprovedAppointmentNotification(event.getAppointment());
      }
      case NOT_APPROVED -> {
        LocalDateTime endDateToTriggerTheScheduler = event.getAppointment().getEndDate();
        schedulerService.scheduleTask(
            () -> {
              if (event.getAppointment().getStatus() == AppointmentStatus.NOT_APPROVED) {
                appointmentService.cancelAppointment(event.getAppointment().getId());
              }
            },
            endDateToTriggerTheScheduler
        );
        mailService.sendNotApprovedAppointmentNotification(event.getAppointment());
      }
      case CANCELED ->
          mailService.sendCanceledAppointmentNotificationToClient(event.getAppointment());
    }
  }
}