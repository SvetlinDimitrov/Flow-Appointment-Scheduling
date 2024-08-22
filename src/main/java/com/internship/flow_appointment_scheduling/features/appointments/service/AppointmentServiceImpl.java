package com.internship.flow_appointment_scheduling.features.appointments.service;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.appointments.utils.AppointmentValidator;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.service.service.ServiceServiceImpl;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.service.UserServiceImpl;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.appointment.AppointmentMapper;
import java.time.LocalDateTime;
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
    Appointment appointment = getAppointmentById(id);

    return switch (dto.status()) {
      case APPROVED, NOT_APPROVED -> {
        User client = appointment.getClient();
        User staff = appointment.getStaff();
        Service service = appointment.getService();

        LocalDateTime endDate = dto.date().plusMinutes(service.getDuration());

        appointmentValidator.validateAppointment(staff, client, service, dto.date(), endDate);

        appointment.setEndDate(endDate);
        appointmentMapper.updateEntity(appointment, dto);
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
