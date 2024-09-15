package com.internship.flow_appointment_scheduling.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.flow_appointment_scheduling.FlowAppointmentSchedulingApplication;
import com.internship.flow_appointment_scheduling.config.TestContainersConfig;
import com.internship.flow_appointment_scheduling.enums.Users;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.ShortAppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.enums.UpdateAppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffDetailsView;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.features.work_space.dto.WorkSpaceView;
import com.internship.flow_appointment_scheduling.infrastructure.mail_service.MailService;
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
@ContextConfiguration(classes = {TestContainersConfig.class,
    FlowAppointmentSchedulingApplication.class})
@AutoConfigureMockMvc
class AppointmentControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private AppointmentRepository appointmentRepository;
  @Autowired
  private ServiceRepository serviceRepository;
  @Autowired
  private UserRepository userRepository;
  @MockBean
  private MailService mailService;

  private static Service VALID_SERVICE;
  private static Appointment VALID_APPOINTMENT;
  private static final long INVALID_SERVICE_ID = -1;
  private static final long INVALID_USER_ID = -1;
  private static final long INVALID_APPOINTMENT_ID = -1;
  private static User VALID_STAFF;
  private static User VALID_CLIENT;
  private static AppointmentCreate VALID_APPOINTMENT_CREATE_DTO;

  @BeforeEach
  void setUp() {
    VALID_STAFF = userRepository.findByEmail(Users.STAFF.getEmail())
        .orElseThrow(() -> new IllegalStateException("No staff data in the database"));

    VALID_CLIENT = userRepository.findByEmail(Users.CLIENT.getEmail())
        .orElseThrow(() -> new IllegalStateException("No client data in the database"));

    VALID_SERVICE = serviceRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No service data in the database"));

    VALID_APPOINTMENT = appointmentRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No appointment data in the database"));

    Service staffService = VALID_STAFF.getServices()
        .getFirst();

    VALID_APPOINTMENT_CREATE_DTO = new AppointmentCreate(
        staffService.getId(),
        Users.CLIENT.getEmail(),
        Users.STAFF.getEmail(),
        LocalDateTime.now()
            .plusYears(2)
            .withHour(VALID_STAFF.getStaffDetails().getBeginWorkingHour().getHour())
            .withMinute(VALID_STAFF.getStaffDetails().getBeginWorkingHour().getMinute())
    );
  }

  @Test
  void getAll_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAll_returnsOk_whenAuthAsAdmin() throws Exception {
    long totalAppointments = appointmentRepository.count();

    mockMvc.perform(get("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAll_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAll_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceId_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + VALID_SERVICE.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceId_returnsOk_whenAuthAsAdmin() throws Exception {
    long totalAppointments = appointmentRepository.countByServiceId(VALID_SERVICE.getId());

    mockMvc.perform(get("/api/v1/appointments/service/" + VALID_SERVICE.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAllByServiceId_returnsOkWithZeroTotalElements_whenInvalidServiceId() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + INVALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void getAllByServiceId_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + VALID_SERVICE.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceId_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + VALID_SERVICE.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceIdAndDate_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + VALID_SERVICE.getId() + "/short")
            .param("date", LocalDate.now().toString()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceIdAndDate_returnsOk_whenAuthAsAdmin() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = appointmentRepository.countByUserIdAndDate(VALID_SERVICE.getId(),
        date);

    mockMvc.perform(get("/api/v1/appointments/service/" + VALID_SERVICE.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByServiceIdAndDate_returnsOk_whenAuthAsClient() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = appointmentRepository.countByServiceIdAndDate(VALID_SERVICE.getId(),
        date);

    mockMvc.perform(get("/api/v1/appointments/service/" + VALID_SERVICE.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByServiceIdAndDate_returnsOk_whenAuthAsStaff() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = appointmentRepository.countByServiceIdAndDate(VALID_SERVICE.getId(),
        date);

    mockMvc.perform(get("/api/v1/appointments/service/" + VALID_SERVICE.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByServiceIdAndDate_returnsOkWithZeroTotalElements_whenInvalidServiceId()
      throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + INVALID_SERVICE_ID + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("date", LocalDate.now().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getAllByServiceIdAndDate_returnsBadRequest_whenDateIsNull() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + INVALID_SERVICE_ID + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("date", (String) null))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getAllByServiceIdAndDate_returnsCorrectlyMappedData_whenAuthAsAdmin() throws Exception {
    LocalDate date = LocalDate.now();

    String response = mockMvc.perform(
            get("/api/v1/appointments/service/" + VALID_SERVICE.getId() + "/short")
                .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
                .param("date", date.toString()))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    List<ShortAppointmentView> result = objectMapper.readValue(response,
        new TypeReference<>() {
        });

    List<ShortAppointmentView> expectedAppointments =
        appointmentRepository.findAllByServiceIdAndDate(VALID_SERVICE.getId(), date)
            .stream()
            .map(this::toShortAppointmentView)
            .toList();

    assertEquals(expectedAppointments, result);
  }

  @Test
  void getAllByUserId_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_CLIENT.getId())
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByUserId_returnsOk_whenAuthAsAdmin() throws Exception {
    long totalAppointments = appointmentRepository.countByUserId(VALID_CLIENT.getId());

    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAllByUserId_returnsOk_whenAuthAsClient_whenGetsForHimSelf() throws Exception {
    long totalAppointments = appointmentRepository.countByUserId(VALID_CLIENT.getId());

    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAllByUserId_returnsForbidden_whenAuthAsClient_whenGetsForOthers()
      throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByUserId_returnsOk_whenAuthAsStaff_whenGetsForHimSelf() throws Exception {
    long totalAppointments = appointmentRepository.countByUserId(VALID_STAFF.getId());

    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAllByUserId_returnsBadRequest_whenAuthAsStaff_whenGetsForOthers() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByUserId_returnsOkWithZeroTotalElements_whenInvalidUserId() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + INVALID_USER_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void getAllByUserIdAndDate_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_CLIENT.getId() + "/short")
            .param("date", LocalDate.now().toString()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByUserIdAndDate_returnsOk_whenAuthAsAdmin() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = appointmentRepository.countByUserIdAndDate(VALID_CLIENT.getId(), date);

    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_CLIENT.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByUserIdAndDate_returnsOk_whenAuthAsClient() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = appointmentRepository.countByUserIdAndDate(VALID_CLIENT.getId(), date);

    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_CLIENT.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByUserIdAndDate_returnsOk_whenAuthAsStaff() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = appointmentRepository.countByUserIdAndDate(VALID_CLIENT.getId(), date);

    mockMvc.perform(get("/api/v1/appointments/user/" + VALID_CLIENT.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByUserIdAndDate_returnsOkWithZeroTotalElements_whenInvalidUserId() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + INVALID_USER_ID + "/short")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("date", LocalDate.now().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getAllByUserIdAndDate_returnsCorrectlyMappedData_whenAuthAsAdmin() throws Exception {
    LocalDate date = LocalDate.now();
    String response = mockMvc.perform(
            get("/api/v1/appointments/user/" + VALID_CLIENT.getId() + "/short")
                .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
                .param("date", date.toString()))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    List<ShortAppointmentView> result = objectMapper.readValue(response,
        new TypeReference<>() {
        });

    List<ShortAppointmentView> expectedAppointments =
        appointmentRepository.findAllByUserIdAndDate(VALID_CLIENT.getId(), date)
            .stream()
            .map(this::toShortAppointmentView)
            .toList();

    assertEquals(expectedAppointments, result);
  }

  @Test
  void getById_returnsNotFound_whenInvalidId() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/" + INVALID_APPOINTMENT_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isNotFound());
  }

  @Test
  void getById_returnsOkWithCorrectlyMappedData_whenValidId_whenAdminAuth() throws Exception {
    String response = mockMvc.perform(get("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    AppointmentView expectedAppointment = toAppointmentView(
        appointmentRepository.findById(VALID_APPOINTMENT.getId())
            .orElseThrow(() -> new IllegalStateException("No appointment data in the database"))
    );

    assertEquals(expectedAppointment, result);
  }

  @Test
  void getById_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/" + VALID_APPOINTMENT.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getById_returnsOk_whenAuthAsAdmin() throws Exception {
    Appointment appointment = appointmentRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No appointment data in the database"));

    String response = mockMvc.perform(get("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    AppointmentView expectedAppointment = toAppointmentView(appointment);

    assertEquals(expectedAppointment, result);
  }

  @Test
  void getById_returnsOk_whenAuthAsStaff_whenGetHisAppointments() throws Exception {
    Optional<Appointment> appointment = VALID_STAFF.getStaffAppointments()
        .stream().findFirst();

    if (appointment.isPresent()) {
      String response = mockMvc.perform(get("/api/v1/appointments/" + appointment.get().getId())
              .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();

      AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
      AppointmentView expectedAppointment = toAppointmentView(appointment.orElse(null));

      assertEquals(expectedAppointment, result);
    }
  }

  @Test
  void getById_returnsForbidden_whenAuthAsStaff_whenStaffGetOthersAppointments() throws Exception {
    Appointment appointment = appointmentRepository.findAll().stream()
        .filter(a -> !a.getStaff().getId().equals(VALID_STAFF.getId()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No appointment data in the database"));

    mockMvc.perform(get("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getById_returnsOk_whenAuthAsClient_whenGetHisAppointments() throws Exception {
    Optional<Appointment> appointment = VALID_CLIENT.getClientAppointments()
        .stream().findFirst();

    if (appointment.isPresent()) {
      String response = mockMvc.perform(get("/api/v1/appointments/" + appointment.get().getId())
              .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();

      AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
      AppointmentView expectedAppointment = toAppointmentView(appointment.get());

      assertEquals(expectedAppointment, result);
    }
  }

  @Test
  void getById_returnsForbidden_whenAuthAsClient_whenGetOtherAppointments() throws Exception {
    Appointment appointment = appointmentRepository.findAll().stream()
        .filter(a -> !a.getClient().getId().equals(VALID_CLIENT.getId()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No appointment data in the database"));

    mockMvc.perform(get("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(post("/api/v1/appointments")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_APPOINTMENT_CREATE_DTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsOk_whenAuthAsAdmin() throws Exception {
    String response = mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_APPOINTMENT_CREATE_DTO)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);

    assertEquals(VALID_APPOINTMENT_CREATE_DTO.serviceId(), result.service().id());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.clientEmail(), result.client().email());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.staffEmail(), result.staff().email());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.date(), result.startDate());

    verify(mailService, times(1)).sendNotApprovedAppointmentNotification(any(Appointment.class));
  }

  @Test
  void create_returnsNotFound_whenAuthAsAdmin_whenStaffEmailDoesNotExists() throws Exception {
    String notExistingStaffEmail = "notExistingStaffEmail@flow.com";

    userRepository.findByEmail(notExistingStaffEmail)
        .ifPresent(userRepository::delete);

    AppointmentCreate appointmentCreate = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        notExistingStaffEmail,
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(appointmentCreate)))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_returnsNotFound_whenAuthAsAdmin_whenClientEmailDoesNotExists() throws Exception {
    String notExistingClientEmail = "notExistingClientEmail@abv.bg";

    userRepository.findByEmail(notExistingClientEmail)
        .ifPresent(userRepository::delete);

    AppointmentCreate appointmentCreate = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        notExistingClientEmail,
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(appointmentCreate)))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_returnsNotFound_whenAuthAsAdmin_whenServiceDoesNotExists() throws Exception {
    Long notExistingServiceId = 99999L;

    serviceRepository.findById(notExistingServiceId)
        .ifPresent(serviceRepository::delete);

    AppointmentCreate appointmentCreate = new AppointmentCreate(
        notExistingServiceId,
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(appointmentCreate)))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_returnsOk_whenAuthAsClient_whenHeIsInTheBody() throws Exception {
    String response = mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_APPOINTMENT_CREATE_DTO)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.serviceId(), result.service().id());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.clientEmail(), result.client().email());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.staffEmail(), result.staff().email());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.date(), result.startDate());

    verify(mailService, times(1)).sendNotApprovedAppointmentNotification(any(Appointment.class));
  }

  @Test
  void create_returnsForbidden_whenAuthAsClient_whenHeIsNotInTheBody() throws Exception {
    User anotherExistingClient = userRepository.findAll()
        .stream()
        .filter(u -> u.getRole() == UserRoles.CLIENT)
        .filter(u -> !u.getEmail().equals(Users.CLIENT.getEmail()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No other client data in the database"));

    AppointmentCreate appointmentCreate = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        anotherExistingClient.getEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(appointmentCreate)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsOk_whenAuthAsStaff_whenHeIsInTheBody() throws Exception {
    String response = mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_APPOINTMENT_CREATE_DTO)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.serviceId(), result.service().id());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.clientEmail(), result.client().email());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.staffEmail(), result.staff().email());
    assertEquals(VALID_APPOINTMENT_CREATE_DTO.date(), result.startDate());

    verify(mailService, times(1)).sendNotApprovedAppointmentNotification(any(Appointment.class));
  }

  @Test
  void create_returnsForbidden_whenAuthAsStaff_whenHeIsNotInTheBody() throws Exception {
    User anotherExistingStaff = userRepository.findAll()
        .stream()
        .filter(u -> u.getRole() == UserRoles.EMPLOYEE)
        .filter(u -> !u.getEmail().equals(Users.STAFF.getEmail()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No other client data in the database"));

    AppointmentCreate appointmentCreate = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        anotherExistingStaff.getEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(appointmentCreate)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsBadRequest_whenServiceIdIsInvalid() throws Exception {

    AppointmentCreate invalidDto = new AppointmentCreate(
        INVALID_SERVICE_ID,
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenClientEmailIsInvalid() throws Exception {
    List<String> invalidEmails = List.of("invalid-email", "");

    for (String invalidEmail : invalidEmails) {
      AppointmentCreate invalidDto = new AppointmentCreate(
          VALID_APPOINTMENT_CREATE_DTO.serviceId(),
          invalidEmail,
          VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
          VALID_APPOINTMENT_CREATE_DTO.date()
      );

      mockMvc.perform(post("/api/v1/appointments")
              .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
              .contentType("application/json")
              .content(objectMapper.writeValueAsString(invalidDto)))
          .andExpect(status().isBadRequest());
    }

    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        null,
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffEmailIsInvalid() throws Exception {
    List<String> invalidEmails = List.of("invalid-email", "");

    for (String invalidEmail : invalidEmails) {
      AppointmentCreate invalidDto = new AppointmentCreate(
          VALID_APPOINTMENT_CREATE_DTO.serviceId(),
          VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
          invalidEmail,
          VALID_APPOINTMENT_CREATE_DTO.date()
      );

      mockMvc.perform(post("/api/v1/appointments")
              .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
              .contentType("application/json")
              .content(objectMapper.writeValueAsString(invalidDto)))
          .andExpect(status().isBadRequest());
    }

    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        null,
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDateIsInvalid() throws Exception {
    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        LocalDateTime.now().minusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffEmailProvidedInsteadOfClient() throws Exception {
    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenClientEmailProvidedInsteadOfStaff() throws Exception {
    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffWorkingTimeDoesNotMatch() throws Exception {
    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        LocalDateTime.now()
            .plusYears(2)
            .withHour(VALID_STAFF.getStaffDetails().getEndWorkingHour().getHour())
            .withMinute(VALID_STAFF.getStaffDetails().getEndWorkingHour().getMinute())
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffAvailabilityIsFalse() throws Exception {
    VALID_STAFF.getStaffDetails().setIsAvailable(false);
    userRepository.save(VALID_STAFF);

    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenServiceAvailabilityIsFalse() throws Exception {
    Service service = serviceRepository.findById(VALID_APPOINTMENT_CREATE_DTO.serviceId())
        .orElseThrow();
    service.setAvailability(false);
    serviceRepository.save(service);

    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffDoesNotContainService() throws Exception {
    VALID_STAFF.getServices().clear();
    userRepository.save(VALID_STAFF);

    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenWorkSpaceCapacityIsReached() throws Exception {
    Service service = serviceRepository.findById(VALID_APPOINTMENT_CREATE_DTO.serviceId())
        .orElseThrow();
    service.getWorkSpace().setAvailableSlots(0);
    serviceRepository.save(service);

    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenOverLappingAppointments() throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(VALID_CLIENT);
    appointment.setStaff(VALID_STAFF);
    appointment.setService(VALID_SERVICE);
    appointment.setStartDate(VALID_APPOINTMENT_CREATE_DTO.date());
    appointment.setEndDate(
        VALID_APPOINTMENT_CREATE_DTO.date().plusMinutes(VALID_SERVICE.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(appointment);

    AppointmentCreate invalidDto = new AppointmentCreate(
        VALID_APPOINTMENT_CREATE_DTO.serviceId(),
        VALID_APPOINTMENT_CREATE_DTO.clientEmail(),
        VALID_APPOINTMENT_CREATE_DTO.staffEmail(),
        VALID_APPOINTMENT_CREATE_DTO.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsForbidden_whenNoAuth() throws Exception {
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromNotApprovedToApproved() throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());

    verify(mailService, times(1)).sendApprovedAppointmentNotification(any(Appointment.class));
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromNotApprovedToCompleted() throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromNotApprovedToCanceled() throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());

    verify(mailService, times(1)).sendCanceledAppointmentNotificationToClient(
        any(Appointment.class));
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromApprovedToCanceled() throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());

    verify(mailService, times(1)).sendCanceledAppointmentNotificationToClient(
        any(Appointment.class));
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromApprovedToCompleted() throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromApprovedToApproved() throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());

    verify(mailService, times(0)).sendApprovedAppointmentNotification(any(Appointment.class));
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromCompletedToCanceled() throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.COMPLETED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());

    verify(mailService, times(1)).sendCanceledAppointmentNotificationToClient(
        any(Appointment.class));
  }

  @Test
  void update_returnsBadRequest_whenValidAuth_whenUpdatingFromCompletedToApproved()
      throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.COMPLETED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenValidAuth_whenUpdatingFromCanceledToApproved()
      throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.CANCELED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenValidAuth_whenUpdatingFromCanceledToCanceled()
      throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.CANCELED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenValidAuth_whenUpdatingFromCanceledToCompleted()
      throws Exception {
    VALID_APPOINTMENT.setStatus(AppointmentStatus.CANCELED);
    appointmentRepository.save(VALID_APPOINTMENT);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsOk_whenAuthAsClient_whenUpdatingOwnAppointment_whenStatusCanceled()
      throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(VALID_CLIENT);
    appointment.setStaff(VALID_STAFF);
    appointment.setService(VALID_SERVICE);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(VALID_SERVICE.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(appointment);

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());

    verify(mailService, times(1)).sendCanceledAppointmentNotificationToClient(
        any(Appointment.class));
  }

  @Test
  void update_returnsBadRequest_whenAuthAsClient_whenUpdatingOwnAppointment_whenStatusApproved()
      throws Exception {
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenAuthAsClient_whenUpdatingOwnAppointment_whenStatusCompleted()
      throws Exception {
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    mockMvc.perform(put("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsForbidden_whenAuthAsClient_whenUpdatingOthersAppointments_whenStatusCanceled()
      throws Exception {
    Appointment appointment = appointmentRepository.findAll()
        .stream()
        .filter(a -> !a.getClient().getId().equals(VALID_CLIENT.getId()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No other client appointments in the database")
        );

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenAuthAsStaff_whenUpdatingOwnAppointment_whenStatusApproved()
      throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(VALID_CLIENT);
    appointment.setStaff(VALID_STAFF);
    appointment.setService(VALID_SERVICE);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(VALID_SERVICE.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(appointment);

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());

    verify(mailService, times(1)).sendApprovedAppointmentNotification(any(Appointment.class));
  }

  @Test
  void update_returnsOk_whenAuthAsStaff_whenUpdatingOwnAppointment_whenStatusCanceled()
      throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(VALID_CLIENT);
    appointment.setStaff(VALID_STAFF);
    appointment.setService(VALID_SERVICE);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(VALID_SERVICE.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(appointment);

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());

    verify(mailService, times(1)).sendCanceledAppointmentNotificationToClient(
        any(Appointment.class));
  }

  @Test
  void update_returnsOk_whenAuthAsStaff_whenUpdatingOwnAppointment_whenStatusCompleted()
      throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(VALID_CLIENT);
    appointment.setStaff(VALID_STAFF);
    appointment.setService(VALID_SERVICE);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(VALID_SERVICE.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(appointment);

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(updateDto.status().name(), result.status().name());
  }

  @Test
  void update_returnsForbidden_whenAuthAsStaff_whenUpdatingOthersAppointments()
      throws Exception {
    Appointment appointment = appointmentRepository.findAll()
        .stream()
        .filter(a -> !a.getStaff().getId().equals(VALID_STAFF.getId()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No other client appointments in the database")
        );

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(delete("/api/v1/appointments/" + VALID_APPOINTMENT.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNoContent_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(delete("/api/v1/appointments/" + VALID_APPOINTMENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isNoContent());

    assertFalse(appointmentRepository.existsById(VALID_APPOINTMENT.getId()));
  }

  @Test
  void delete_returnsNoContent_whenAuthAsClient_whenDeletingOwnAppointment() throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(VALID_CLIENT);
    appointment.setStaff(VALID_STAFF);
    appointment.setService(VALID_SERVICE);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(VALID_SERVICE.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(appointment);

    mockMvc.perform(delete("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isNoContent());

    assertFalse(appointmentRepository.existsById(appointment.getId()));

    verify(mailService, times(1)).sendCanceledAppointmentNotificationToClient(
        any(Appointment.class));
  }

  @Test
  void delete_returnsForbidden_whenAuthAsClient_whenDeletingOthersAppointment() throws Exception {
    Appointment appointment = appointmentRepository.findAll()
        .stream()
        .filter(a -> !a.getClient().getId().equals(VALID_CLIENT.getId()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No other client appointments in the database"));

    mockMvc.perform(delete("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNoContent_whenAuthAsStaff_whenDeletingOwnAppointment() throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(VALID_CLIENT);
    appointment.setStaff(VALID_STAFF);
    appointment.setService(VALID_SERVICE);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(VALID_SERVICE.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(appointment);

    mockMvc.perform(delete("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isNoContent());

    assertFalse(appointmentRepository.existsById(appointment.getId()));

    verify(mailService, times(1)).sendCanceledAppointmentNotificationToClient(
        any(Appointment.class));
  }

  @Test
  void delete_returnsForbidden_whenAuthAsStaff_whenDeletingOthersAppointment() throws Exception {
    Appointment appointment = appointmentRepository.findAll()
        .stream()
        .filter(a -> !a.getStaff().getId().equals(VALID_STAFF.getId()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No other staff appointments in the database"));

    mockMvc.perform(delete("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNotFound_whenAppointmentDoesNotExist() throws Exception {
    mockMvc.perform(delete("/api/v1/appointments/" + INVALID_APPOINTMENT_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isNotFound());
  }

  private String generateBarrierAuthHeader(String userEmail) {
    String token = jwtService.generateToken(userEmail).jwtToken().token();
    return "Bearer " + token;
  }

  private AppointmentView toAppointmentView(Appointment appointment) {
    return new AppointmentView(
        appointment.getId(),
        toUserView(appointment.getClient()),
        toUserView(appointment.getStaff()),
        appointment.getStartDate(),
        appointment.getEndDate(),
        appointment.getStatus(),
        toServiceView(appointment.getService())
    );
  }

  private ShortAppointmentView toShortAppointmentView(Appointment appointment) {
    return new ShortAppointmentView(
        appointment.getId(),
        appointment.getService().getName(),
        appointment.getStartDate(),
        appointment.getEndDate(),
        appointment.getStatus()
    );
  }

  private UserView toUserView(User entity) {
    return new UserView(
        entity.getId(),
        entity.getFirstName(),
        entity.getLastName(),
        entity.getEmail(),
        entity.getRole().name(),
        toStaffDetailsView(entity)
    );
  }

  private StaffDetailsView toStaffDetailsView(User entity) {
    if (entity.getStaffDetails() == null) {
      return null;
    }
    return new StaffDetailsView(
        entity.getStaffDetails().getSalary(),
        entity.getStaffDetails().getProfit(),
        entity.getStaffDetails().getCompletedAppointments(),
        entity.getStaffDetails().getIsAvailable(),
        entity.getStaffDetails().getStartDate(),
        entity.getStaffDetails().getBeginWorkingHour(),
        entity.getStaffDetails().getEndWorkingHour(),
        entity.getServices().stream()
            .map(Service::getId)
            .collect(Collectors.toList())
    );
  }

  private ServiceView toServiceView(Service service) {
    return new ServiceView(
        service.getId(),
        service.getName(),
        service.getDuration(),
        service.getDescription(),
        service.getAvailability(),
        service.getPrice().doubleValue(),
        new WorkSpaceView(
            service.getWorkSpace().getName(),
            service.getWorkSpace().getAvailableSlots()
        )
    );
  }
}