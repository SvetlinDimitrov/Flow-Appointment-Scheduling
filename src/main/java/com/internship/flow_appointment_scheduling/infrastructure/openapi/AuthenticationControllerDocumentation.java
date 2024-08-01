package com.internship.flow_appointment_scheduling.infrastructure.openapi;

import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtResponse;
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
              examples = @ExampleObject(
                  name = "AuthenticationRequestExample",
                  value = "{" +
                      "\"email\": \"abv@example.com\"," +
                      "\"password\": \"password123A!\"" +
                      "}"
              )
          )
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created authentication token",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))
      ),
      @ApiResponse(responseCode = "400", description = "Invalid input",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProblemDetail.class))}),
  })
  @PostMapping
  ResponseEntity<JwtResponse> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest);

}
