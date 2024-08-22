package com.internship.flow_appointment_scheduling.features.appointments.service;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

  Page<AppointmentView> getAll(Pageable pageable);

  Page<AppointmentView> getAllByUserEmail(String userEmail, Pageable pageable);

  Page<AppointmentView> getAllByServiceId(Long serviceId, Pageable pageable);

  AppointmentView getById(Long id);

  AppointmentView create(AppointmentCreate dto);

  AppointmentView update(Long id, AppointmentUpdate dto);

  void delete(Long id);

//  List<AppointmentView> getAllUserEmailAndDay(String userEmail, String day);
}
