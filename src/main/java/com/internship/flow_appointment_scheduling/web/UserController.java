package com.internship.flow_appointment_scheduling.web;

import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeHireDto;
import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeModifyDto;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import com.internship.flow_appointment_scheduling.infrastructure.openapi.UserControllerDocumentation;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocumentation {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'EMPLOYEE')")
  public ResponseEntity<Page<UserView>> getAll(Pageable pageable) {
    return ResponseEntity.ok(userService.getAll(pageable));
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
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @permissionEvaluator.halfClientEmployeeAccess(authentication, #id)")
  public ResponseEntity<UserView> update(@PathVariable Long id,
      @Valid @RequestBody UserPutRequest updateDto) {
    return ResponseEntity.ok(userService.update(id, updateDto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR') || @permissionEvaluator.halfClientEmployeeAccess(authentication, #id)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("/hire")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<UserView> hireEmployee(@Valid @RequestBody EmployeeHireDto hireDto) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userService.hireEmployee(hireDto));
  }

  @PutMapping("/{id}/employee")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
  public ResponseEntity<UserView> modifyEmployee(@PathVariable Long id,
      @Valid @RequestBody EmployeeModifyDto modifyDto) {
    return ResponseEntity.ok(userService.modifyEmployee(id, modifyDto));
  }
}
