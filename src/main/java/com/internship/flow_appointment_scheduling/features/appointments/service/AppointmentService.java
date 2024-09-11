package com.internship.flow_appointment_scheduling.features.appointments.service;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.ShortAppointmentView;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

  Page<AppointmentView> getAll(Pageable pageable);

  Page<AppointmentView> getAllByUserId(Long userId, Pageable pageable);

  Page<AppointmentView> getAllByServiceId(Long serviceId, Pageable pageable);

  List<ShortAppointmentView> getAllByUserIdAndDate(Long userId, LocalDate date);

  List<ShortAppointmentView> getAllByServiceIdAndDate(Long serviceId, LocalDate date);

  AppointmentView getById(Long id);

  AppointmentView create(AppointmentCreate dto);

  AppointmentView update(Long id, AppointmentUpdate dto);

  void delete(Long id);

  void cancelAppointment(Long id);

}
