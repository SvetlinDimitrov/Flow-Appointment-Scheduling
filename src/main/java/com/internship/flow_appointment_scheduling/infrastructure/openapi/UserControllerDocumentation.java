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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserControllerDocumentation {

  @Operation(summary = "Get all users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the users",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Page.class))}),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json")}),
  })
  @GetMapping
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<Page<UserView>> getAll(Pageable pageable);

  @Operation(summary = "Get a user by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the user",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserView.class))}),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProblemDetail.class))}),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json")}),
  })
  @SecurityRequirement(name = "bearerAuth")
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
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProblemDetail.class))}),
  })
  @PostMapping
  ResponseEntity<UserView> create(@Valid @RequestBody UserPostRequest createDto);

  @Operation(
      summary = "Update an existing user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserPutRequest.class),
              examples = {
                  @ExampleObject(
                      name = "Admin Request",
                      value = "{" +
                          "\"firstName\": \"John2\"," +
                          "\"lastName\": \"Wick2\"," +
                          "\"role\": \"CLIENT\"" +
                          "}"
                  ),
                  @ExampleObject(
                      name = "Normal Request",
                      value = "{" +
                          "\"firstName\": \"Jane\"," +
                          "\"lastName\": \"Doe\"" +
                          "}"
                  )
              }
          )
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserView.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid input",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProblemDetail.class))}),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProblemDetail.class))}),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json")}),
  })
  @SecurityRequirement(name = "bearerAuth")
  @PutMapping("/{id}")
  ResponseEntity<UserView> update(@PathVariable Long id,
      @Valid @RequestBody UserPutRequest updateDto);

  @Operation(summary = "Delete a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProblemDetail.class))}),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json")}),
  })
  @SecurityRequirement(name = "bearerAuth")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id);
}