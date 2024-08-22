package com.internship.flow_appointment_scheduling.infrastructure.openapi;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface AppointmentControllerDocumentation {

  @Operation(summary = "Get all appointments", description = "Retrieve a paginated list of all appointments")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the appointments",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Page.class))}),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json")}),
  })
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<Page<AppointmentView>> getAll(Pageable pageable);

  @Operation(summary = "Get appointment by ID", description = "Retrieve an appointment by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the appointment",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = AppointmentView.class))}),
      @ApiResponse(responseCode = "404", description = "Appointment not found",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json")}),
  })
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<AppointmentView> getById(
      @Parameter(description = "ID of the appointment to be retrieved") @PathVariable Long id);

  @Operation(summary = "Create a new appointment", description = "Create a new appointment with the provided details",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AppointmentCreate.class),
              examples = @ExampleObject(value = """
                      {
                        "serviceId": 1,
                        "clientEmail": "alice.smith@example.com",
                        "staffEmail": "jane.doe@example.com",
                        "date": "2025-10-01T10:00:00"
                      }
                  """)
          )
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Appointment created",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = AppointmentView.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid input",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json")}),
  })
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<AppointmentView> create(@RequestBody @Valid AppointmentCreate dto);

  @Operation(summary = "Update an appointment",
      description = "Update an existing appointment with the provided details",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AppointmentUpdate.class),
              examples = @ExampleObject(value = """
                      {
                        "status": "APPROVED",
                        "date": "2025-10-01T10:00:00"
                      }
                  """)
          )
      )
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Appointment updated",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = AppointmentView.class))}),
      @ApiResponse(responseCode = "404", description = "Appointment not found",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = @Content),
  })
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<AppointmentView> update(
      @Parameter(description = "ID of the appointment to be updated") @PathVariable Long id,
      @RequestBody @Valid AppointmentUpdate dto);

  @Operation(summary = "Delete an appointment", description = "Delete an appointment by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Appointment deleted",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Appointment not found",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json")}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json")}),
  })
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<Void> delete(
      @Parameter(description = "ID of the appointment to be deleted") @PathVariable Long id);
}