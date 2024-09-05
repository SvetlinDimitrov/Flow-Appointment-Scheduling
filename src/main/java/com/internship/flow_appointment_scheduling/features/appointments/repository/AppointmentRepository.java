package com.internship.flow_appointment_scheduling.features.appointments.repository;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

  Page<Appointment> findAllByServiceId(Long serviceId, Pageable pageable);

  List<Appointment> findAllByStatusIn(List<AppointmentStatus> statuses);

  @Query("SELECT a FROM Appointment a " +
      "WHERE a.status <> :status " +
      "AND a.endDate < :currentDateTime")
  List<Appointment> findAllByStatusAndEndDateBefore(@Param("status") AppointmentStatus status,
      @Param("currentDateTime") LocalDateTime currentDateTime);

  @Query("SELECT a FROM Appointment a " +
      "WHERE a.client.id = :userId " +
      "OR a.staff.id = :userId")
  Page<Appointment> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT a FROM Appointment a " +
      "WHERE (a.client.id = :userId OR a.staff.id = :userId) " +
      "AND FUNCTION('DATE', a.startDate) = :date")
  List<Appointment> findAllByUserIdAndDate(@Param("userId") Long userId,
      @Param("date") LocalDate date);

  @Query("SELECT a FROM Appointment a " +
      "WHERE a.service.id = :serviceId " +
      "AND FUNCTION('DATE', a.startDate) = :date")
  List<Appointment> findAllByServiceIdAndDate(@Param("serviceId") Long serviceId,
      @Param("date") LocalDate date);

  @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
      "FROM Appointment a " +
      "WHERE (a.client.email = :email OR a.staff.email = :email) " +
      "AND a.startDate <= :endDate " +
      "AND a.endDate >= :startDate " +
      "AND (a.status = 'APPROVED' OR a.status = 'NOT_APPROVED')")
  boolean existsOverlappingAppointment(@Param("email") String email,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  @Query("SELECT COUNT(a) " +
      "FROM Appointment a " +
      "JOIN a.service s " +
      "WHERE s.workSpace.id = :workSpaceId " +
      "AND a.startDate <= :endDate " +
      "AND a.endDate >= :startDate " +
      "AND (a.status = 'APPROVED' OR a.status = 'NOT_APPROVED')")
  int countAppointmentsInWorkspace(@Param("workSpaceId") Long workSpaceId,
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}