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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
                          "\"email\": \"john.wick@example.com\"," +
                          "\"password\": \"password123A!\"" +
                          "}"
                  ),
                  @ExampleObject(
                      name = "Employee Example",
                      value = "{" +
                          "\"email\": \"jane.doe@example.com\"," +
                          "\"password\": \"password123A!\"" +
                          "}"
                  ),
                  @ExampleObject(
                      name = "Customer Example",
                      value = "{" +
                          "\"email\": \"alice.smith@example.com\"," +
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
}
