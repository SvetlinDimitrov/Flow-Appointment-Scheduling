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
  public ResponseEntity<Page<UserView>> getAllUsers(Pageable pageable) {
    return new ResponseEntity<>(userService.getAllUsers(pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserView> getUserById(@PathVariable Long id) {
    return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<UserView> createUser(@Valid @RequestBody UserPostRequest createDto) {
    return new ResponseEntity<>(userService.createUser(createDto), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserView> updateUser(@PathVariable Long id, @Valid @RequestBody UserPutRequest updateDto) {
    return new ResponseEntity<>(userService.updateUser(id, updateDto), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
