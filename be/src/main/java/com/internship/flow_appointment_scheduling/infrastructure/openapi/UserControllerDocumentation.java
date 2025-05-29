package com.internship.flow_appointment_scheduling.infrastructure.openapi;

import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffHireDto;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffModifyDto;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPasswordUpdate;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserControllerDocumentation {

  @Operation(summary = "Get all users", description = "Accessible by ADMINISTRATOR, EMPLOYEE roles")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the users",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Page.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = {@Content(mediaType = "application/json")}),
      })
  @GetMapping
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<Page<UserView>> getAll(
      Pageable pageable, @RequestParam(required = false) UserRoles userRole);

  @Operation(
      summary = "Get all users by service ID",
      description =
          "Retrieve a paginated list of users associated with a specific service ID. Accessible by ADMINISTRATOR, EMPLOYEE, and CLIENT roles.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the users",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = Page.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = {@Content(mediaType = "application/json")}),
      })
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping("/service/{serviceId}")
  ResponseEntity<Page<UserView>> getAllByServiceId(Pageable pageable, @PathVariable Long serviceId);

  @Operation(
      summary = "Get a user by ID",
      description = "Accessible by ADMINISTRATOR, EMPLOYEE, and CLIENT roles")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the user",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserView.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProblemDetail.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = {@Content(mediaType = "application/json")}),
      })
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping("/{id}")
  ResponseEntity<UserView> getById(@PathVariable Long id);

  @Operation(
      summary = "Create a new user",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UserPostRequest.class),
                      examples =
                          @ExampleObject(
                              name = "UserPostBodyExample",
                              value =
                                  "{"
                                      + "\"firstName\": \"John\","
                                      + "\"lastName\": \"Wick\","
                                      + "\"email\": \"client2@abv.bg\","
                                      + "\"password\": \"password123A!\""
                                      + "}"))))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserView.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProblemDetail.class))
            }),
      })
  @PostMapping
  ResponseEntity<UserView> create(@Valid @RequestBody UserPostRequest createDto);

  @Operation(
      summary = "Update an existing user",
      description = "Accessible by ADMINISTRATOR, EMPLOYEE, and CLIENT roles",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UserPutRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Normal Request",
                            value =
                                "{" + "\"firstName\": \"Jane\"," + "\"lastName\": \"Doe\"" + "}")
                      })))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserView.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProblemDetail.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProblemDetail.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = {@Content(mediaType = "application/json")}),
      })
  @SecurityRequirement(name = "bearerAuth")
  @PutMapping("/{id}")
  ResponseEntity<UserView> update(
      @PathVariable Long id, @Valid @RequestBody UserPutRequest updateDto);

  @Operation(
      summary = "Delete a user",
      description = "Accessible by ADMINISTRATOR, EMPLOYEE, and CLIENT roles")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User deleted", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProblemDetail.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = {@Content(mediaType = "application/json")}),
      })
  @SecurityRequirement(name = "bearerAuth")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id);

  @Operation(
      description = "Accessible by ADMINISTRATOR role",
      summary = "Hire a new staff",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = StaffHireDto.class),
                      examples =
                          @ExampleObject(
                              name = "StaffHireBodyExample",
                              value =
                                  "{"
                                      + "\"userInfo\": {"
                                      + "\"firstName\": \"John\","
                                      + "\"lastName\": \"Doe\","
                                      + "\"email\": \"staff2@abv.bg\","
                                      + "\"password\": \"password123A!\""
                                      + "},"
                                      + "\"staffDetailsDto\": {"
                                      + "\"salary\": 50000,"
                                      + "\"beginWorkingHour\": \"09:00\","
                                      + "\"endWorkingHour\": \"17:00\""
                                      + "}"
                                      + "}"))))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Staff hired",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserView.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProblemDetail.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = {@Content(mediaType = "application/json")}),
      })
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping("/hire")
  ResponseEntity<UserView> hireStaff(@Valid @RequestBody StaffHireDto hireDto);

  @Operation(
      summary = "Modify an existing staff",
      description = "Accessible by ADMINISTRATOR and EMPLOYEE roles",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = StaffModifyDto.class),
                      examples = {
                        @ExampleObject(
                            name = "AdminModifyBodyExample",
                            value =
                                "{"
                                    + "\"userRole\": \"EMPLOYEE\","
                                    + "\"salary\": 50000,"
                                    + "\"isAvailable\": true,"
                                    + "\"beginWorkingHour\": \"09:00\","
                                    + "\"endWorkingHour\": \"17:00\""
                                    + "}"),
                        @ExampleObject(
                            name = "StaffModifyBodyExample",
                            value =
                                "{"
                                    + "\"isAvailable\": true,"
                                    + "\"beginWorkingHour\": \"09:00\","
                                    + "\"endWorkingHour\": \"17:00\""
                                    + "}")
                      })))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Staff modified",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserView.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProblemDetail.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Staff not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProblemDetail.class))
            }),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = {@Content(mediaType = "application/json")}),
      })
  @SecurityRequirement(name = "bearerAuth")
  @PutMapping("/{id}/staff")
  ResponseEntity<UserView> modifyStaff(
      @PathVariable Long id, @Valid @RequestBody StaffModifyDto modifyDto);

  @Operation(
      summary = "Reset user password",
      description =
          "Accessible by ADMINISTRATOR and users with appropriate permissions. Allows a user to reset their password.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UserPasswordUpdate.class),
                      examples =
                          @ExampleObject(
                              name = "UserPasswordUpdateExample",
                              value = "{" + "\"newPassword\": \"newPassword123A!\"" + "}"))))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password reset successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserView.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(mediaType = "application/json"))
      })
  @SecurityRequirement(name = "bearerAuth")
  @PutMapping("/reset-password")
  ResponseEntity<UserView> resetPassword(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody UserPasswordUpdate dto);
}
