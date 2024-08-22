package com.internship.flow_appointment_scheduling.features.appointment.repository;

import com.internship.flow_appointment_scheduling.features.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

}
