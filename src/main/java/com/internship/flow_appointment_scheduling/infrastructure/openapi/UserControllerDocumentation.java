package com.internship.flow_appointment_scheduling.infrastructure.openapi;

import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface UserControllerDocumentation {

  @Operation(summary = "Get all users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the users",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Page.class))})
  })
  @GetMapping
  ResponseEntity<Page<UserView>> getAll(Pageable pageable);

  @Operation(summary = "Get a user by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the user",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserView.class))}),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = @Content)
  })
  @GetMapping("/{id}")
  ResponseEntity<UserView> getById(@PathVariable Long id);

  @Operation(
      summary = "Create a new user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserPostRequest.class),
              examples = @ExampleObject(
                  name = "UserPostBodyExample",
                  value = "{" +
                      "\"firstName\": \"John\"," +
                      "\"lastName\": \"Wick\"," +
                      "\"email\": \"abv@example.com\"," +
                      "\"role\": \"CLIENT\"," +
                      "\"password\": \"password123A!\"" +
                      "}"
              )
          )
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserView.class))
      ),
      @ApiResponse(responseCode = "400", description = "Invalid input",
          content = @Content)
  })
  @PostMapping
  ResponseEntity<UserView> create(@Valid @RequestBody UserPostRequest createDto);

  @Operation(
      summary = "Update an existing user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserPutRequest.class),
              examples = @ExampleObject(
                  name = "UserPutBodyExample",
                  value = "{" +
                      "\"firstName\": \"John2\"," +
                      "\"lastName\": \"Wick2\"," +
                      "\"role\": \"CLIENT\"" +
                      "}"
              )
          )
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserView.class))}),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid input",
          content = @Content)
  })
  @PutMapping("/{id}")
  ResponseEntity<UserView> update(@PathVariable Long id, @Valid @RequestBody UserPutRequest updateDto);

  @Operation(summary = "Delete a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = @Content)
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id);
}