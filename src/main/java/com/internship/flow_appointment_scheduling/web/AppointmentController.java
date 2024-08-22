package com.internship.flow_appointment_scheduling.web;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.ShortAppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.service.AppointmentService;
import com.internship.flow_appointment_scheduling.infrastructure.openapi.AppointmentControllerDocumentation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController implements AppointmentControllerDocumentation {

  private final AppointmentService appointmentService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<Page<AppointmentView>> getAll(Pageable pageable) {
    return ResponseEntity.ok(appointmentService.getAll(pageable));
  }

  @GetMapping("/service/{serviceId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<Page<AppointmentView>> getAllByServiceId(
      @PathVariable Long serviceId,
      Pageable pageable) {
    return ResponseEntity.ok(appointmentService.getAllByServiceId(serviceId, pageable));
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @userPermissionEvaluator.currentClientOrStaffAccess(authentication, #userId)")
  public ResponseEntity<Page<AppointmentView>> getAllByUserId(
      @PathVariable Long userId,
      Pageable pageable) {
    return ResponseEntity.ok(appointmentService.getAllByUserId(userId, pageable));
  }

  @GetMapping("/user/{userId}/short")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @userPermissionEvaluator.currentClientOrStaffAccess(authentication, #userId)")
  public ResponseEntity<List<ShortAppointmentView>> getAllByUserIdAndDate(
      @PathVariable Long userId,
      @RequestParam LocalDate date) {
    return ResponseEntity.ok(appointmentService.getAllByUserIdAndDate(userId, date));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @appointmentPermissionEvaluator.currentClientOrStaffAccess(authentication, #id)")
  public ResponseEntity<AppointmentView> getById(@PathVariable Long id) {
    return ResponseEntity.ok(appointmentService.getById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @appointmentPermissionEvaluator.currentClientOrStaffAccess(authentication, #dto)")
  public ResponseEntity<AppointmentView> create(@RequestBody @Valid AppointmentCreate dto) {
    return ResponseEntity.ok(appointmentService.create(dto));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @appointmentPermissionEvaluator.currentClientOrStaffAccess(authentication, #id)")
  public ResponseEntity<AppointmentView> update(
      @PathVariable Long id,
      @RequestBody @Valid AppointmentUpdate dto) {
    return ResponseEntity.ok(appointmentService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @appointmentPermissionEvaluator.currentClientOrStaffAccess(authentication, #id)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    appointmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
