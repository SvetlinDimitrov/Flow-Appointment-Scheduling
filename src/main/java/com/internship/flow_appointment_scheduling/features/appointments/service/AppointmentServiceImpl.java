package com.internship.flow_appointment_scheduling.features.appointments.service;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.service.service.ServiceServiceImpl;
import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.service.UserServiceImpl;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.appointment.AppointmentMapper;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final AppointmentMapper appointmentMapper;

  private final UserServiceImpl userService;
  private final ServiceServiceImpl serviceService;

  @Override
  public Page<AppointmentView> getAll(Pageable pageable) {
    return appointmentRepository
        .findAll(pageable)
        .map(appointmentMapper::toView);
  }

  @Override
  public Page<AppointmentView> getAllByUserEmail(String userEmail, Pageable pageable) {
    return appointmentRepository
        .findAllByUserEmail(userEmail, pageable)
        .map(appointmentMapper::toView);
  }

  @Override
  public Page<AppointmentView> getAllByServiceId(Long serviceId, Pageable pageable) {
    return appointmentRepository
        .findAllByServiceId(serviceId, pageable)
        .map(appointmentMapper::toView);
  }

  @Override
  public AppointmentView getById(Long id) {
    return appointmentMapper.toView(getAppointmentById(id));
  }

  @Override
  public AppointmentView create(AppointmentCreate dto) {
    User client = userService.findByEmail(dto.clientEmail());
    User staff = userService.findByEmail(dto.staffEmail());
    Service service = serviceService.findById(dto.serviceId());

    LocalDateTime endDate = dto.date().plusMinutes(service.getDuration());

    checkForUserRoles(client, staff);
    checkForStaffWorkingTime(staff, dto.date(), endDate);
    checkForStaffAndServiceAvailability(staff, service);
    checkForOverlappingAppointments(client, staff, dto.date(), endDate);

    Appointment appointment = appointmentMapper.toEntity(dto);
    appointment.setEndDate(endDate);
    appointment.setClient(client);
    appointment.setStaff(staff);
    appointment.setService(service);

    return appointmentMapper.toView(appointmentRepository.save(appointment));
  }

  @Override
  public AppointmentView update(Long id, AppointmentUpdate dto) {
    Appointment appointment = getAppointmentById(id);

    Optional.ofNullable(dto.date()).ifPresent(date -> {
      User client = appointment.getClient();
      User staff = appointment.getStaff();
      Service service = appointment.getService();

      LocalDateTime endDate = date.plusMinutes(service.getDuration());

      checkForStaffWorkingTime(staff, date, endDate);
      checkForStaffAndServiceAvailability(staff, service);
      checkForOverlappingAppointments(client, staff, date, endDate);

      appointment.setEndDate(endDate);
    });

    appointmentMapper.updateEntity(appointment, dto);

    return appointmentMapper.toView(appointmentRepository.save(appointment));
  }

  @Override
  public void delete(Long id) {
    appointmentRepository.delete(getAppointmentById(id));
  }

  private Appointment getAppointmentById(Long id) {
    return appointmentRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(Exceptions.APPOINTMENT_NOT_FOUND, id));
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

  private void checkForStaffAndServiceAvailability(User staff, Service service) {
    if (!staff.getStaffDetails().getIsAvailable()) {
      throw new BadRequestException(Exceptions.APPOINTMENT_STAFF_NOT_AVAILABLE, staff.getEmail());
    }

    if (!service.getAvailability()) {
      throw new BadRequestException(Exceptions.APPOINTMENT_SERVICE_NOT_AVAILABLE, service.getId());
    }
  }

  private boolean hasOverLappingAppointmentsForGivenUser(
      User user, LocalDateTime startDate, LocalDateTime endDate) {
    return appointmentRepository.existsOverlappingAppointment(
        user.getEmail(), startDate, endDate
    );
  }
}
