package com.internship.flow_appointment_scheduling.features.appointments.service;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.ShortAppointmentView;
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

    appointmentValidator.validateAppointment(staff, client, service, dto.date(), endDate , null);

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
      The following logic is applied:
      1. When the status is canceled or completed, the date is not required,
      and it will not be used.
      2. When the status is approved or not approved,
      the date is required to track the validations.
      3. If the user sending the request is the client,
      he can only change the status of the appointment
      to cancel or not_approved, or exception will be thrown.
      4. If the appointment is canceled or completed, it should not be able to be modified.
      My imagination is that the appointment is canceled and that's all,
      it will be garbage collected by the system.
      The same goes for the completed status.
      Because imagine that you receive in the email that the appointment is canceled,
      and after a minute you receive that is moved to another date.
      In my opinion, it will be better to just create a new appointment for that.
    */
    Appointment appointment = getAppointmentById(id);

    if(appointment.getStatus().equals(AppointmentStatus.CANCELED) ||
        appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
      throw new BadRequestException(Exceptions.APPOINTMENT_CANNOT_BE_MODIFIED);
    }

    return switch (dto.status()) {
      case APPROVED, NOT_APPROVED -> {
        User client = appointment.getClient();
        User staff = appointment.getStaff();
        Service service = appointment.getService();

        LocalDateTime endDate = dto.date().plusMinutes(service.getDuration());

        //TODO:: Send email to the client and staff in a feature milestone
        appointmentValidator.validateAppointment(staff, client, service, dto.date(), endDate , appointment.getId());

        appointment.setEndDate(endDate);
        appointmentMapper.updateEntity(appointment, dto);
        yield appointmentMapper.toView(appointmentRepository.save(appointment));
      }
      case COMPLETED -> {
        appointment.setStatus(dto.status());
        userService.handleCompletingTheAppointment(appointment);
        yield appointmentMapper.toView(appointmentRepository.save(appointment));
      }
      default -> {
        appointment.setStatus(dto.status());
        yield appointmentMapper.toView(appointmentRepository.save(appointment));
      }
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
