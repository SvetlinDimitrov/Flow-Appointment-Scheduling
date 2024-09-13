package com.internship.flow_appointment_scheduling.web;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.ShortAppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.enums.UpdateAppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.service.AppointmentService;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AppointmentService appointmentService;

  @MockBean
  private JwtService jwtService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
    SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void getAll_returnsOk_withValidParams() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    Page<AppointmentView> appointmentPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(appointmentService.getAll(pageable)).thenReturn(appointmentPage);

    mockMvc.perform(get("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getAllByServiceId_returnsOk_withValidParams() throws Exception {
    Long serviceId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    Page<AppointmentView> appointmentPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(appointmentService.getAllByServiceId(serviceId, pageable)).thenReturn(appointmentPage);

    mockMvc.perform(
            get("/api/v1/appointments/service/{serviceId}", serviceId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getAllByServiceIdAndDate_returnsOk_withValidParams() throws Exception {
    Long serviceId = 1L;
    LocalDate date = LocalDate.of(2023, 10, 1);
    List<ShortAppointmentView> shortAppointmentViews = Arrays.asList(
        new ShortAppointmentView(1L, "Service 1", null, null,
            AppointmentStatus.CANCELED),
        new ShortAppointmentView(2L, "Service 2", null, null,
            AppointmentStatus.APPROVED)
    );

    when(appointmentService.getAllByServiceIdAndDate(serviceId, date)).thenReturn(
        shortAppointmentViews);

    mockMvc.perform(
            get("/api/v1/appointments/service/{serviceId}/short", serviceId)
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].serviceName").value("Service 1"))
        .andExpect(jsonPath("$[1].id").value(2L))
        .andExpect(jsonPath("$[1].serviceName").value("Service 2"));
  }

  @Test
  void getAllByServiceIdAndDate_returnsNotFound_whenServiceDoesNotExist() throws Exception {
    Long nonExistentServiceId = 999L;
    LocalDate date = LocalDate.of(2023, 10, 1);

    when(appointmentService.getAllByServiceIdAndDate(nonExistentServiceId, date))
        .thenThrow(new NotFoundException(Exceptions.SERVICE_NOT_FOUND));

    mockMvc.perform(get("/api/v1/appointments/service/{serviceId}/short", nonExistentServiceId)
            .param("date", date.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAllByUserId_returnsOk_withValidParams() throws Exception {
    Long userId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    Page<AppointmentView> appointmentPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(appointmentService.getAllByUserId(userId, pageable)).thenReturn(appointmentPage);

    mockMvc.perform(get("/api/v1/appointments/user/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getAllByUserIdAndDate_returnsOk_withValidParams() throws Exception {
    Long userId = 1L;
    LocalDate date = LocalDate.of(2023, 10, 1);
    List<ShortAppointmentView> shortAppointmentViews = Arrays.asList(
        new ShortAppointmentView(1L, "Service 1", null, null,
            AppointmentStatus.APPROVED),
        new ShortAppointmentView(2L, "Service 2", null, null,
            AppointmentStatus.CANCELED)
    );

    when(appointmentService.getAllByUserIdAndDate(userId, date)).thenReturn(shortAppointmentViews);

    mockMvc.perform(get("/api/v1/appointments/user/{userId}/short", userId)
            .param("date", date.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].serviceName").value("Service 1"))
        .andExpect(jsonPath("$[0].status").value(AppointmentStatus.APPROVED.toString()))
        .andExpect(jsonPath("$[1].id").value(2L))
        .andExpect(jsonPath("$[1].serviceName").value("Service 2"))
        .andExpect(jsonPath("$[1].status").value(AppointmentStatus.CANCELED.toString()));
  }

  @Test
  void getAllByUserIdAndDate_returnsNotFound_whenUserDoesNotExist() throws Exception {
    Long nonExistentUserId = 999L;
    LocalDate date = LocalDate.of(2023, 10, 1);

    when(appointmentService.getAllByUserIdAndDate(nonExistentUserId, date))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND));

    mockMvc.perform(get("/api/v1/appointments/user/{userId}/short", nonExistentUserId)
            .param("date", date.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getById_returnsOk_withValidId() throws Exception {
    Long id = 1L;
    AppointmentView appointmentView = new AppointmentView(
        id, null, null, null, null, AppointmentStatus.APPROVED, null
    );

    when(appointmentService.getById(id)).thenReturn(appointmentView);

    mockMvc.perform(get("/api/v1/appointments/{id}", id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.status").value(AppointmentStatus.APPROVED.toString()));
  }

  @Test
  void getById_returnsNotFound_whenIdDoesNotExist() throws Exception {
    Long nonExistentId = 999L;

    when(appointmentService.getById(nonExistentId))
        .thenThrow(new NotFoundException(Exceptions.APPOINTMENT_NOT_FOUND));

    mockMvc.perform(get("/api/v1/appointments/{id}", nonExistentId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_returnsOk_withValidDto() throws Exception {
    AppointmentCreate dto = new AppointmentCreate(
        1L,
        "client@example.com",
        "staff@example.com",
        LocalDateTime.now().plusDays(1)
    );

    AppointmentView appointmentView = new AppointmentView(
        1L, null, null, null, null, null, null
    );

    when(appointmentService.create(dto)).thenReturn(appointmentView);

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  void create_returnsBadRequest_withInvalidServiceId() throws Exception {

    AppointmentCreate invalidDtoNullServiceId = new AppointmentCreate(
        null,
        "client@example.com",
        "staff@example.com",
        LocalDateTime.now().plusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoNullServiceId)))
        .andExpect(status().isBadRequest());

    AppointmentCreate invalidDtoMinServiceId = new AppointmentCreate(
        0L,
        "client@example.com",
        "staff@example.com",
        LocalDateTime.now().plusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoMinServiceId)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_withInvalidStaffEmail() throws Exception {
    AppointmentCreate invalidDtoBlankStaffEmail = new AppointmentCreate(
        1L,
        "client@example.com",
        "",
        LocalDateTime.now().plusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoBlankStaffEmail)))
        .andExpect(status().isBadRequest());

    AppointmentCreate invalidDtoInvalidStaffEmail = new AppointmentCreate(
        1L,
        "client@example.com",
        "invalid-email",
        LocalDateTime.now().plusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoInvalidStaffEmail)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_withInvalidDate() throws Exception {

    AppointmentCreate invalidDtoNullDate = new AppointmentCreate(
        1L,
        "client@example.com",
        "staff@example.com",
        null
    );

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoNullDate)))
        .andExpect(status().isBadRequest());

    AppointmentCreate invalidDtoPastDate = new AppointmentCreate(
        1L,
        "client@example.com",
        "staff@example.com",
        LocalDateTime.now().minusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoPastDate)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_withInvalidClientEmail() throws Exception {
    AppointmentCreate invalidDtoBlankClientEmail = new AppointmentCreate(
        1L,
        "",
        "staff@example.com",
        LocalDateTime.now().plusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoBlankClientEmail)))
        .andExpect(status().isBadRequest());

    AppointmentCreate invalidDtoInvalidClientEmail = new AppointmentCreate(
        1L,
        "invalid-email",
        "staff@example.com",
        LocalDateTime.now().plusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoInvalidClientEmail)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsOk_withValidDto() throws Exception {
    Long id = 1L;
    AppointmentUpdate dto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);
    AppointmentView appointmentView = new AppointmentView(id, null, null, null, null,
        AppointmentStatus.APPROVED, null);

    when(appointmentService.update(id, dto)).thenReturn(appointmentView);

    mockMvc.perform(put("/api/v1/appointments/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.status").value(UpdateAppointmentStatus.APPROVED.toString()));
  }

  @Test
  void update_returnsNotFound_whenIdDoesNotExist() throws Exception {
    Long nonExistentId = 999L;
    AppointmentUpdate dto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    when(appointmentService.update(nonExistentId, dto))
        .thenThrow(new NotFoundException(Exceptions.APPOINTMENT_NOT_FOUND));

    mockMvc.perform(put("/api/v1/appointments/{id}", nonExistentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_returnsBadRequest_withInvalidDto() throws Exception {
    Long id = 1L;
    AppointmentUpdate invalidDtoNullStatus = new AppointmentUpdate(null);

    mockMvc.perform(put("/api/v1/appointments/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDtoNullStatus)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void delete_returnsNoContent_withValidId() throws Exception {
    Long id = 1L;

    mockMvc.perform(delete("/api/v1/appointments/{id}", id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void delete_returnsNotFound_whenIdDoesNotExist() throws Exception {
    Long nonExistentId = 999L;

    doThrow(new NotFoundException(Exceptions.APPOINTMENT_NOT_FOUND))
        .when(appointmentService).delete(nonExistentId);

    mockMvc.perform(delete("/api/v1/appointments/{id}", nonExistentId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}