package com.internship.flow_appointment_scheduling.web;

import com.internship.flow_appointment_scheduling.features.service.annotations.employee_or_admin.EmployeeOrAdmin;
import com.internship.flow_appointment_scheduling.features.service.dto.ServicePostPutRequest;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.service.service.ServiceService;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

  private final ServiceService serviceService;

  public ServiceController(ServiceService serviceService) {
    this.serviceService = serviceService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'EMPLOYEE', 'CLIENT')")
  public ResponseEntity<Page<ServiceView>> getAll(Pageable pageable,
      @RequestParam(required = false) String employeeEmail) {
    return ResponseEntity.ok(serviceService.getAll(pageable, employeeEmail));
  }

  @GetMapping("/id")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'EMPLOYEE', 'CLIENT')")
  public ResponseEntity<ServiceView> getById(@RequestParam Long id) {
    return ResponseEntity.ok(serviceService.getById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<ServiceView> create(
      @Valid @RequestBody ServicePostPutRequest createDto,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    return ResponseEntity.ok(serviceService.create(createDto, customUserDetails.getUsername()));
  }

  @PostMapping("/assign")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<ServiceView> assignEmployee(
      @RequestParam Long serviceId,
      @RequestParam @EmployeeOrAdmin String employeeEmail) {
    return ResponseEntity.ok(serviceService.assignEmployee(serviceId, employeeEmail));
  }

  @DeleteMapping("/unassign")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<ServiceView> unassignEmployee(
      @RequestParam Long serviceId,
      @RequestParam String employeeEmail) {
    return ResponseEntity.ok(serviceService.unassignEmployee(serviceId, employeeEmail));
  }

  @PutMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<ServiceView> update(
      @RequestParam Long id,
      @Valid @RequestBody ServicePostPutRequest putDto) {
    return ResponseEntity.ok(serviceService.update(id, putDto));
  }

  @DeleteMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public void delete(@RequestParam Long id) {
    serviceService.delete(id);
  }
}
