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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentValidator {

  private final AppointmentRepository appointmentRepository;

  public void validateAppointment(User staff, User client, Service service,
      LocalDateTime startDate, LocalDateTime endDate, Long appointmentId) {

     /*
      Steps to create a new appointment:
      1. Check if the client is a client and the staff is a staff
      (not applied for the administrator role).
      2. Check if the staff is available at the given time.
      3. Check if staff and service are available.
      4. Check if staff has the requested service.
      5. Check if the client and staff have overlapping appointments
      (they should be able to have only one appointment at a given time).
      6. Check if the workSpace has the capacity for the given time.
     */

    checkForUserRoles(client, staff);
    checkForStaffWorkingTime(staff, startDate, endDate);
    checkForStaffAndServiceAvailability(staff, service);
    checkForStaffContainingService(staff, service);
    checkForWorkSpaceCapacity(service, startDate, endDate);
    Optional.ofNullable(appointmentId)
        .ifPresentOrElse(
            id -> checkForOverlappingAppointmentsNotIncludingTheCurrentOne(
                client, staff, startDate, endDate, id),
            () -> checkForOverlappingAppointments(client, staff, startDate, endDate));
  }


  private void checkForUserRoles(User client, User staff) {
    if (!client.getRole().equals(UserRoles.CLIENT)) {
      throw new BadRequestException(Exceptions.APPOINTMENT_WRONG_CLIENT_ROLE, client.getEmail());
    }

    if (!staff.getRole().equals(UserRoles.EMPLOYEE)) {
      throw new BadRequestException(Exceptions.APPOINTMENT_WRONG_STAFF_ROLE, staff.getEmail());
    }
  }

  private void checkForStaffWorkingTime(
      User staff, LocalDateTime startDate, LocalDateTime endDate) {
    LocalTime startTime = startDate.toLocalTime();
    LocalTime endTime = endDate.toLocalTime();
    StaffDetails staffDetails = staff.getStaffDetails();

    if (startTime.isBefore(staffDetails.getBeginWorkingHour()) ||
        endTime.isAfter(staffDetails.getEndWorkingHour())) {
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

  private void checkForOverlappingAppointmentsNotIncludingTheCurrentOne(
      User client, User staff, LocalDateTime startDate, LocalDateTime endDate, Long appId) {
    /*
     You have an example where you have an appointment for 11 am to 12 pm.
     But you want to move it to be in between 10:30 am to 11:30 am.
     If I just check for overlapping appointments, it will return the same appointment, and it
     tells me that I have an overlapping appointment witch is not true
     because I want to move it.
     This is why I added the appId parameter.
     Now I will check overlapping appointments, and I will not include the current one.
    */

    if (hasOverLappingAppointmentsForGivenUser(client, startDate, endDate, appId)) {
      throw new BadRequestException(Exceptions.APPOINTMENT_OVERLAP);
    }

    if (hasOverLappingAppointmentsForGivenUser(staff, startDate, endDate, appId)) {
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
          Exceptions.APPOINTMENT_STAFF_NOT_CONTAINING_SERVICE,
          staff.getEmail(), service.getId()
      );
    }
  }

  private void checkForWorkSpaceCapacity(Service service, LocalDateTime startDate,
      LocalDateTime endDate) {

    /*
      Here, in order for a service to be executed, it needs a workingSpace.
      Each working space has available slots which stands for
      (how many staff members (employees) can execute a service at a given time).
      I am checking if the working space has the available slots for the given time.
    */

    int appointmentsCount = appointmentRepository.countAppointmentsInWorkspace(
        service.getWorkSpace().getId(), startDate, endDate);

    if (appointmentsCount >= service.getWorkSpace().getAvailableSlots()) {
      throw new BadRequestException(Exceptions.APPOINTMENT_WORK_SPACE_NOT_AVAILABLE,
          service.getWorkSpace().getId());
    }
  }

  private boolean hasOverLappingAppointmentsForGivenUser(
      User user, LocalDateTime startDate, LocalDateTime endDate) {
    /*
     In here, I am checking if their area any overlapping appointments for the given user.
     I am checking only the Approved ones because I think
     it will be better if the staff can receive multiple requests for the same date and time,
     and he can choose which one to approve.
    */
    return appointmentRepository.existsOverlappingAppointment(user.getEmail(), startDate, endDate);
  }

  private boolean hasOverLappingAppointmentsForGivenUser(
      User user, LocalDateTime startDate, LocalDateTime endDate, Long appId) {
    /*
     In here, I am checking if their area any overlapping appointments for the given user.
     I am checking only the Approved ones because I think
     it will be better if the staff can receive multiple requests for the same date and time,
     and he can choose which one to approve.
    */
    return appointmentRepository.existsOverlappingAppointment(user.getEmail(), startDate, endDate,
        appId);
  }
}
