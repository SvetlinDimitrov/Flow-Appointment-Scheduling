package com.intership.flow_appointment_scheduling.web;


import com.intership.flow_appointment_scheduling.feature.user.dto.UserPostRequest;
import com.intership.flow_appointment_scheduling.feature.user.dto.UserPutRequest;
import com.intership.flow_appointment_scheduling.feature.user.dto.UserView;
import com.intership.flow_appointment_scheduling.feature.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<Page<UserView>> getAll(Pageable pageable) {
    return ResponseEntity.ok(userService.getAll(pageable));
  }

  @GetMapping("/{id}")
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
  public ResponseEntity<UserView> update(@PathVariable Long id, @Valid @RequestBody UserPutRequest updateDto) {
    return ResponseEntity.ok(userService.update(id, updateDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
