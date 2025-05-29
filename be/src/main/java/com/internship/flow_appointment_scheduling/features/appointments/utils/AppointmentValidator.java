package com.internship.flow_appointment_scheduling.features.appointments.utils;

import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentValidator {

  private final AppointmentRepository appointmentRepository;

  /**
   * Validates an appointment based on various criteria.
   *
   * <p>This is how this validates method works:
   *
   * <ul>
   *   <li>First, it checks if the staff user in the DTO is actually a staff and the same goes for
   *       the client. This is done in the method {@code checkForUserRoles}.
   *   <li>Second, it checks if the staff is actually available at the given time. This is done in
   *       the method {@code checkForStaffWorkingTime}, where it gets the staff's starting working
   *       hours and ending working hours and verifies if the startDate and endDate for the
   *       appointment are in between.
   *   <li>Third, it checks if the staff and services are available. In the database, both the
   *       StaffDetails and the services have a field called availability. It checks if these two
   *       fields are true. This is done in the method {@code checkForStaffAndServiceAvailability}.
   *   <li>Fourth, it checks if the staff actually has the requested service that the DTO is
   *       sending. This is done in the method {@code checkForStaffContainingService}.
   *   <li>Fifth, it checks if the staff member has another appointment in the range between the
   *       starting date and the ending date. This is done in the method {@code
   *       checkForOverlappingAppointments}, which looks for any appointments that have a status of
   *       either NOT_APPROVED or APPROVED. It checks for NOT_APPROVED too because only one request
   *       for an appointment in a specific time is allowed.
   *   <li>Sixth, it checks if the workspace has available spaces. In the database, the workspace
   *       entity has a field called available_slots. For example, if a workspace has 4
   *       available_slots, it means that at a specific time, there can be only 4 appointments
   *       executed. This is checked in the method {@code checkForWorkSpaceCapacity}. If there are
   *       more than 4 appointments, an exception will be thrown indicating that the workspace is
   *       full.
   * </ul>
   *
   * @param staff the staff user
   * @param client the client user
   * @param service the service
   * @param startDate the start date and time of the appointment
   * @param endDate the end date and time of the appointment
   * @throws BadRequestException if any validation fails
   */
  public void validateAppointment(
      User staff, User client, Service service, LocalDateTime startDate, LocalDateTime endDate) {

    checkForUserRoles(client, staff);
    checkForStaffWorkingTime(staff, startDate, endDate);
    checkForStaffAndServiceAvailability(staff, service);
    checkForStaffContainingService(staff, service);
    checkForWorkSpaceCapacity(service, startDate, endDate);
    checkForOverlappingAppointments(client, staff, startDate, endDate);
  }

  private void checkForUserRoles(User client, User staff) {
    if (UserRoles.CLIENT != client.getRole()) {
      throw new BadRequestException(Exceptions.APPOINTMENT_WRONG_CLIENT_ROLE, client.getEmail());
    }

    if (UserRoles.EMPLOYEE != staff.getRole()) {
      throw new BadRequestException(Exceptions.APPOINTMENT_WRONG_STAFF_ROLE, staff.getEmail());
    }
  }

  private void checkForStaffWorkingTime(
      User staff, LocalDateTime startDate, LocalDateTime endDate) {
    LocalTime startTime = startDate.toLocalTime();
    LocalTime endTime = endDate.toLocalTime();
    StaffDetails staffDetails = staff.getStaffDetails();

    if (startTime.isBefore(staffDetails.getBeginWorkingHour())
        || endTime.isAfter(staffDetails.getEndWorkingHour())) {
      throw new BadRequestException(Exceptions.APPOINTMENT_STAFF_NOT_AVAILABLE, staff.getEmail());
    }
  }

  private void checkForOverlappingAppointments(
      User client, User staff, LocalDateTime startDate, LocalDateTime endDate) {
    if (hasOverLappingAppointmentsForGivenUser(client, startDate, endDate)) {
      throw new BadRequestException(Exceptions.APPOINTMENT_OVERLAP);
    }

    if (hasOverLappingAppointmentsForGivenUser(staff, startDate, endDate)) {
      throw new BadRequestException(Exceptions.APPOINTMENT_OVERLAP);
    }
  }

  private void checkForStaffAndServiceAvailability(User staff, Service service) {
    if (!staff.getStaffDetails().getIsAvailable()) {
      throw new BadRequestException(Exceptions.APPOINTMENT_STAFF_NOT_AVAILABLE, staff.getEmail());
    }

    if (!service.getAvailability()) {
      throw new BadRequestException(Exceptions.APPOINTMENT_SERVICE_NOT_AVAILABLE, service.getId());
    }
  }

  private void checkForStaffContainingService(User staff, Service service) {
    if (!staff.getServices().contains(service)) {
      throw new BadRequestException(
          Exceptions.APPOINTMENT_STAFF_NOT_CONTAINING_SERVICE, staff.getEmail(), service.getId());
    }
  }

  private void checkForWorkSpaceCapacity(
      Service service, LocalDateTime startDate, LocalDateTime endDate) {

    int appointmentsCount =
        appointmentRepository.countAppointmentsInWorkspace(
            service.getWorkSpace().getId(), startDate, endDate);

    if (appointmentsCount >= service.getWorkSpace().getAvailableSlots()) {
      throw new BadRequestException(
          Exceptions.APPOINTMENT_WORK_SPACE_NOT_AVAILABLE, service.getWorkSpace().getId());
    }
  }

  private boolean hasOverLappingAppointmentsForGivenUser(
      User user, LocalDateTime startDate, LocalDateTime endDate) {
    return appointmentRepository.existsOverlappingAppointment(user.getEmail(), startDate, endDate);
  }
}
