package com.internship.flow_appointment_scheduling.web;

import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffHireDto;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffModifyDto;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import com.internship.flow_appointment_scheduling.infrastructure.openapi.UserControllerDocumentation;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocumentation {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'EMPLOYEE')")
  public ResponseEntity<Page<UserView>> getAll(Pageable pageable,
      @RequestParam(required = false) UserRoles userRole) {
    return ResponseEntity.ok(userService.getAll(pageable , userRole));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'EMPLOYEE') || @permissionEvaluator.halfClientAccess(authentication, #id)")
  public ResponseEntity<UserView> getById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getById(id));
  }

  @PostMapping
  public ResponseEntity<UserView> create(@Valid @RequestBody UserPostRequest createDto) {

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userService.create(createDto));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @permissionEvaluator.halfClientStaffAccess(authentication, #id)")
  public ResponseEntity<UserView> update(@PathVariable Long id,
      @Valid @RequestBody UserPutRequest updateDto) {
    return ResponseEntity.ok(userService.update(id, updateDto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @permissionEvaluator.halfClientStaffAccess(authentication, #id)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("/hire")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<UserView> hireStaff(@Valid @RequestBody StaffHireDto hireDto) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userService.hireStaff(hireDto));
  }

  @PutMapping("/{id}/staff")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @permissionEvaluator.currentStaffAccessOnly(authentication, #id)")
  public ResponseEntity<UserView> modifyStaff(@PathVariable Long id,
      @Valid @RequestBody StaffModifyDto modifyDto) {
    return ResponseEntity.ok(userService.modifyStaff(id, modifyDto));
  }
}
