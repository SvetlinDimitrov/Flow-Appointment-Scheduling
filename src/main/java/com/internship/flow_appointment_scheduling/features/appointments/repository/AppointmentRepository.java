package com.internship.flow_appointment_scheduling.features.appointments.repository;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  Page<Appointment> findAllByServiceId(Long serviceId, Pageable pageable);

  @Query("SELECT a FROM Appointment a WHERE a.client.email = :email OR a.staff.email = :email")
  Page<Appointment> findAllByUserEmail(@Param("email") String email, Pageable pageable);

  @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
      "FROM Appointment a " +
      "WHERE (a.client.email = :email OR a.staff.email = :email) " +
      "AND (a.startDate < :endDate AND a.endDate > :startDate)")
  boolean existsOverlappingAppointment(@Param("email") String email,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}