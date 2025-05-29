package com.internship.flow_appointment_scheduling.web;

import com.internship.flow_appointment_scheduling.features.service.annotations.staff_or_admin.StaffOrAdmin;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.service.ServiceService;
import com.internship.flow_appointment_scheduling.features.work_space.service.WorkSpaceService;
import com.internship.flow_appointment_scheduling.infrastructure.openapi.ServiceControllerDocumentation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController implements ServiceControllerDocumentation {

  private final ServiceService serviceService;
  private final WorkSpaceService workSpaceService;

  @GetMapping
  public ResponseEntity<Page<ServiceView>> getAll(Pageable pageable,
      @RequestParam(required = false) String staffEmail) {
    return ResponseEntity.ok(serviceService.getAll(pageable, staffEmail));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ServiceView> getById(@PathVariable Long id) {
    return ResponseEntity.ok(serviceService.getById(id));
  }

  @GetMapping("/workspaces")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<List<String>> getAllWorkSpacesNames() {
    return ResponseEntity.ok(workSpaceService.getAllNames());
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<ServiceView> create(
      @Valid @RequestBody ServiceDTO createDto) {
    return ResponseEntity.ok(serviceService.create(createDto));
  }

  @PostMapping("/{id}/assign")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<ServiceView> assignStaff(
      @PathVariable Long id,
      @RequestParam @StaffOrAdmin String staffEmail) {
    return ResponseEntity.ok(serviceService.assignStaff(id, staffEmail));
  }

  @PutMapping("/{id}/unassign")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<ServiceView> unassignStaff(
      @PathVariable Long id,
      @RequestParam String staffEmail) {
    return ResponseEntity.ok(serviceService.unassignStaff(id, staffEmail));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<ServiceView> update(
      @PathVariable Long id,
      @Valid @RequestBody ServiceDTO putDto) {
    return ResponseEntity.ok(serviceService.update(id, putDto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    serviceService.delete(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
