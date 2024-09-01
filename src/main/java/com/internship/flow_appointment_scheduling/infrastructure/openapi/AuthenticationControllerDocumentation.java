package com.internship.flow_appointment_scheduling.infrastructure.openapi;

import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenPostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface AuthenticationControllerDocumentation {

  @Operation(
      summary = "Create authentication token",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AuthenticationRequest.class),
              examples = {
                  @ExampleObject(
                      name = "Admin Example",
                      value = "{" +
                          "\"email\": \"admin1@flow.com\"," +
                          "\"password\": \"password123A!\"" +
                          "}"
                  ),
                  @ExampleObject(
                      name = "Staff Example",
                      value = "{" +
                          "\"email\": \"staff1@flow.com\"," +
                          "\"password\": \"password123A!\"" +
                          "}"
                  ),
                  @ExampleObject(
                      name = "Customer Example",
                      value = "{" +
                          "\"email\": \"client1@abv.bg\"," +
                          "\"password\": \"password123A!\"" +
                          "}"
                  )
              }
          )
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created authentication token",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))
      ),
      @ApiResponse(responseCode = "400", description = "Invalid input",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProblemDetail.class))}),
  })
  @PostMapping
  ResponseEntity<AuthenticationResponse> createAuthenticationToken(
      @Valid @RequestBody AuthenticationRequest authenticationRequest);

  @Operation(
      summary = "Refresh authentication token",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = RefreshTokenPostRequest.class)
          )
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully refreshed authentication token",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))
      ),
      @ApiResponse(responseCode = "400", description = "Invalid input or token expired",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProblemDetail.class))}),
  })
  @PostMapping("/refresh")
  ResponseEntity<AuthenticationResponse> refreshToken(
      @Valid @RequestBody RefreshTokenPostRequest dto);

  @Operation(
      summary = "Reset password",
      description = "Send email with reset password link"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password reset email sent successfully"),
      @ApiResponse(responseCode = "404", description = "User not found",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
      @ApiResponse(responseCode = "400", description = "Bad request",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
  })
  @GetMapping("/reset-password")
  ResponseEntity<Void> resetPassword(@RequestParam String email);
}
