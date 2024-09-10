package com.internship.flow_appointment_scheduling.infrastructure.scheduler;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.user.service.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentCleanupScheduler {

  private static final Logger logger = LoggerFactory.getLogger(AppointmentCleanupScheduler.class);

  private final AppointmentRepository appointmentRepository;
  private final UserServiceImpl userService;

  /**
   * This scheduler will run every 15 minutes to check the appointments.
   *
   * <p>Every appointment that has a status of COMPLETED will trigger a staff update method which
   * will modify the total completed appointments and the profit that the current staff has made.
   * Then, the appointment will be deleted.</p>
   *
   * <p>If the appointment has a status of CANCELED, it will be deleted as well.</p>
   *
   * <p>After that, the scheduler will check for expired appointments. An appointment is considered
   * expired if it has a status of NOT_APPROVED and the current date is after the endDate.
   * Additionally, an appointment with an APPROVED status and an endDate that is more than 3 days in
   * the past will also be considered expired. If an appointment is expired, it will be
   * deleted.</p>
   */
  @Scheduled(fixedRate = 15 * 60 * 1000)
  public void cleanUpAppointments() {
    logger.info("Starting cleanup of appointments");

    List<Appointment> finishedAppointments = appointmentRepository.findAllByStatusIn(
        List.of(AppointmentStatus.CANCELED, AppointmentStatus.COMPLETED)
    );

    finishedAppointments.forEach(appointment -> {
      if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
        userService.handleCompletingTheAppointment(appointment);
        logger.info("Handled completed appointment with ID: {}", appointment.getId());
      }
      appointmentRepository.delete(appointment);
      logger.info("Deleted appointment with ID: {}", appointment.getId());
    });

    List<Appointment> expiredNotApprovedAppointments = appointmentRepository.findAllByStatusAndEndDateBefore(
        AppointmentStatus.NOT_APPROVED, LocalDateTime.now());

    List<Appointment> expiredApprovedAppointments = appointmentRepository.findAllByStatusAndEndDateBefore(
        AppointmentStatus.APPROVED, LocalDateTime.now().plusDays(3));

    logger.info("Deleted {} expired appointments", expiredNotApprovedAppointments.size() + expiredApprovedAppointments.size());

    appointmentRepository.deleteAll(expiredNotApprovedAppointments);
    appointmentRepository.deleteAll(expiredApprovedAppointments);

    logger.info("Finished cleanup of appointments");
  }
}