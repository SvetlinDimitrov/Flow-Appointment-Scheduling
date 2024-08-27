package com.internship.flow_appointment_scheduling.features.appointments.service;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.ShortAppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.enums.UpdateAppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.appointments.utils.AppointmentValidator;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.service.service.ServiceServiceImpl;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.service.UserServiceImpl;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.appointment.AppointmentMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

  private final AppointmentValidator appointmentValidator;

  @Override
  public Page<AppointmentView> getAll(Pageable pageable) {
    return appointmentRepository
        .findAll(pageable)
        .map(appointmentMapper::toView);
  }

  @Override
  public Page<AppointmentView> getAllByUserId(Long userId, Pageable pageable) {
    return appointmentRepository
        .findAllByUserId(userId, pageable)
        .map(appointmentMapper::toView);
  }

  @Override
  public List<ShortAppointmentView> getAllByUserIdAndDate(Long userId, LocalDate date) {
    return appointmentRepository
        .findAllByUserIdAndDate(userId, date)
        .stream()
        .map(appointmentMapper::toViewShort)
        .toList();
  }

  @Override
  public List<ShortAppointmentView> getAllByServiceIdAndDate(Long serviceId, LocalDate date) {
    return appointmentRepository
        .findAllByServiceIdAndDate(serviceId, date)
        .stream()
        .map(appointmentMapper::toViewShort)
        .toList();
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

    appointmentValidator.validateAppointment(staff, client, service, dto.date(), endDate);

    Appointment appointment = appointmentMapper.toEntity(dto);
    appointment.setEndDate(endDate);
    appointment.setClient(client);
    appointment.setStaff(staff);
    appointment.setService(service);

    return appointmentMapper.toView(appointmentRepository.save(appointment));
  }

  @Override
  public AppointmentView update(Long id, AppointmentUpdate dto) {
    /*
    Functionality:
      1) Provide a way for the client to cancel the appointment.
      2) Provide a way for the staff to approve, cancel or complete the appointment.
    Considerations:
      1) The client cannot approve or complete the appointment.
      2) Ones the appointment is canceled or completed, it cannot be modified.
      It will be garbage collected after a certain period of time.
    */
    Appointment appointment = getAppointmentById(id);

    if(appointment.getStatus().equals(AppointmentStatus.CANCELED) ||
        appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
      throw new BadRequestException(Exceptions.APPOINTMENT_CANNOT_BE_MODIFIED);
    }

    appointmentMapper.updateEntity(appointment, dto);

    return switch (dto.status()) {
      /*
          Appointment status APPROVED is set once when the staff approves the appointment.
          After that user cannot provide this status.
      */
      //TODO:: Send email to the client and staff in a feature milestone
      case APPROVED -> appointmentMapper.toView(appointmentRepository.save(appointment));
      case COMPLETED -> {
        userService.handleCompletingTheAppointment(appointment);
        yield appointmentMapper.toView(appointmentRepository.save(appointment));
      }
      case CANCELED -> appointmentMapper.toView(appointmentRepository.save(appointment));
    };
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
}
