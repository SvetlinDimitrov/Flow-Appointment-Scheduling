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

  /**
   * Creates a new appointment based on the provided AppointmentCreate DTO.
   * <p>
   * This is how this creates method works:
   * <ul>
   *   <li>When the AppointmentCreate DTO is sent, the method checks if the user has either clientEmail or staffEmail, or if the user is an administrator.</li>
   *   <li>This is because administrators can create appointments for any client and staff member, but another staff member, for example, cannot create an appointment for another staff member.</li>
   *   <li>If this happens, it will result in a 403 error, which is handled at the controller level.</li>
   *   <li>Then, the method checks if everything is here. If not, a 404 error will be thrown.</li>
   *   <li>Before that, the method checks in the DTO if everything is valid. If not, a 400 error will be thrown.</li>
   *   <li>The logic here is to get the date from the DTO, which will be the starting date.</li>
   *   <li>Then, the method gets the duration of the service and uses it to create the endDate.</li>
   *   <li>In the AppointmentValidator, the logic for validating the appointment based on the hours is separated. The validator has documentation explaining what is happening there.</li>
   *   <li>After that, the appointment is saved.</li>
   * </ul>
   *
   * @param dto the data transfer object containing the appointment creation information
   * @return the created appointment view
   * @throws BadRequestException if the DTO is invalid (400 error)
   * @throws NotFoundException if the user is unauthorized (404 error)
   */
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

  /**
   * Updates an appointment based on the provided ID and update data transfer object (DTO).
   * <p>
   * Functionality:
   * <ul>
   *   <li>Provide a way for the client to cancel the appointment.</li>
   *   <li>Provide a way for the staff to approve, cancel, or complete the appointment.</li>
   * </ul>
   * Considerations:
   * <ul>
   *   <li>The client cannot approve or complete the appointment.</li>
   *   <li>Once the appointment is canceled or completed, it cannot be modified. It will be garbage collected after a certain period of time.</li>
   * </ul>
   * Status Handling:
   * <ul>
   *   <li>If the status is NOT_APPROVED, then any status is accepted.</li>
   *   <li>If the status was APPROVED, then in the next request, either CANCELED or COMPLETED is expected. If another APPROVED status is sent, then nothing will happen.</li>
   *   <li>If the status was CANCELED or COMPLETED and another request is sent again to modify the status, a BadRequestException will be thrown.</li>
   * </ul>
   *
   * @param id the ID of the appointment to update
   * @param dto the data transfer object containing the update information
   * @return the updated appointment view
   * @throws BadRequestException if the appointment is already canceled or completed
   */
  @Override
  public AppointmentView update(Long id, AppointmentUpdate dto) {
    Appointment appointment = getAppointmentById(id);

    if (AppointmentStatus.CANCELED == appointment.getStatus() ||
        AppointmentStatus.COMPLETED == appointment.getStatus()) {
      throw new BadRequestException(Exceptions.APPOINTMENT_CANNOT_BE_MODIFIED);
    }

    appointmentMapper.updateEntity(appointment, dto);


    return switch (dto.status()) {
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
