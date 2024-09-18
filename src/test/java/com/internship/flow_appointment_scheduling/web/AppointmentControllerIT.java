package com.internship.flow_appointment_scheduling.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.internship.flow_appointment_scheduling.FlowAppointmentSchedulingApplication;
import com.internship.flow_appointment_scheduling.config.TestContainersConfig;
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
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtService;
import com.internship.flow_appointment_scheduling.seed.enums.SeededAdminUsers;
import com.internship.flow_appointment_scheduling.seed.enums.SeededClientUsers;
import com.internship.flow_appointment_scheduling.seed.enums.SeededStaffUsers;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "duke"))
      .withPerMethodLifecycle(true);

  private static Service validService;
  private static Appointment validAppointment;
  private static final long INVALID_SERVICE_ID = -1;
  private static final long INVALID_USER_ID = -1;
  private static final long INVALID_APPOINTMENT_ID = -1;
  private static User validStaff;
  private static User validClient;
  private static AppointmentCreate validAppointmentCreateDto;
  private static final String NOT_APPROVED_SUBJECT = "Appointment Request Created Successfully";
  private static final String APPROVED_SUBJECT = "Appointment Created Successfully";
  private static final String CANCELED_SUBJECT = "Appointment Canceled";

  @BeforeEach
  void setUp() {
    validStaff = userRepository.findByEmail(SeededStaffUsers.STAFF1.getEmail())
        .orElseThrow(() -> new IllegalStateException("No staff data in the database"));

    validClient = userRepository.findByEmail(SeededClientUsers.CLIENT1.getEmail())
        .orElseThrow(() -> new IllegalStateException("No client data in the database"));

    validService = serviceRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No service data in the database"));

    validAppointment = appointmentRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No appointment data in the database"));

    Service staffService = validStaff.getServices()
        .getFirst();

    validAppointmentCreateDto = new AppointmentCreate(
        staffService.getId(),
        SeededClientUsers.CLIENT1.getEmail(),
        SeededStaffUsers.STAFF1.getEmail(),
        LocalDateTime.now()
            .plusYears(2)
            .withHour(validStaff.getStaffDetails().getBeginWorkingHour().getHour())
            .withMinute(validStaff.getStaffDetails().getBeginWorkingHour().getMinute())
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
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAll_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAll_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceId_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + validService.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceId_returnsOk_whenAuthAsAdmin() throws Exception {
    long totalAppointments = appointmentRepository.countByServiceId(validService.getId());

    mockMvc.perform(get("/api/v1/appointments/service/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAllByServiceId_returnsOkWithZeroTotalElements_whenInvalidServiceId() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + INVALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void getAllByServiceId_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceId_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceIdAndDate_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + validService.getId() + "/short")
            .param("date", LocalDate.now().toString()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceIdAndDate_returnsOk_whenAuthAsAdmin() throws Exception {
    LocalDate date = LocalDate.now();
    long result = validService.getAppointments()
        .stream()
        .filter(a -> a.getStartDate().toLocalDate().equals(date))
        .count();

    mockMvc.perform(get("/api/v1/appointments/service/" + validService.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(result));
  }

  @Test
  void getAllByServiceIdAndDate_returnsOk_whenAuthAsClient() throws Exception {
    LocalDate date = LocalDate.now();
    long result = validService.getAppointments()
        .stream()
        .filter(a -> a.getStartDate().toLocalDate().equals(date))
        .count();

    mockMvc.perform(get("/api/v1/appointments/service/" + validService.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(result));
  }

  @Test
  void getAllByServiceIdAndDate_returnsOk_whenAuthAsStaff() throws Exception {
    LocalDate date = LocalDate.now();
    long result = validService.getAppointments()
        .stream()
        .filter(a -> a.getStartDate().toLocalDate().equals(date))
        .count();

    mockMvc.perform(get("/api/v1/appointments/service/" + validService.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(result));
  }

  @Test
  void getAllByServiceIdAndDate_returnsOkWithZeroTotalElements_whenInvalidServiceId()
      throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + INVALID_SERVICE_ID + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("date", LocalDate.now().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getAllByServiceIdAndDate_returnsBadRequest_whenDateIsNull() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/service/" + INVALID_SERVICE_ID + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("date", (String) null))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getAllByServiceIdAndDate_returnsCorrectlyMappedData_whenAuthAsAdmin() throws Exception {
    LocalDate date = LocalDate.now();

    String response = mockMvc.perform(
            get("/api/v1/appointments/service/" + validService.getId() + "/short")
                .header("Authorization",
                    generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
                .param("date", date.toString()))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    List<ShortAppointmentView> result = objectMapper.readValue(response,
        new TypeReference<>() {
        });

    List<ShortAppointmentView> expectedAppointments =
        appointmentRepository.findAllByServiceIdAndDate(validService.getId(), date)
            .stream()
            .map(this::toShortAppointmentView)
            .toList();

    assertEquals(expectedAppointments, result);
  }

  @Test
  void getAllByUserId_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + validClient.getId())
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByUserId_returnsOk_whenAuthAsAdmin() throws Exception {
    int totalAppointments = validClient.getClientAppointments().size();

    mockMvc.perform(get("/api/v1/appointments/user/" + validClient.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAllByUserId_returnsOk_whenAuthAsClient_whenGetsForHimSelf() throws Exception {
    int totalAppointments = validClient.getClientAppointments().size();

    mockMvc.perform(get("/api/v1/appointments/user/" + validClient.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAllByUserId_returnsForbidden_whenAuthAsClient_whenGetsForOthers()
      throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + validStaff.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByUserId_returnsOk_whenAuthAsStaff_whenGetsForHimSelf() throws Exception {
    int totalAppointments = validStaff.getStaffAppointments().size();

    mockMvc.perform(get("/api/v1/appointments/user/" + validStaff.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(totalAppointments));
  }

  @Test
  void getAllByUserId_returnsBadRequest_whenAuthAsStaff_whenGetsForOthers() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + validClient.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByUserId_returnsOkWithZeroTotalElements_whenInvalidUserId() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + INVALID_USER_ID)
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void getAllByUserIdAndDate_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + validClient.getId() + "/short")
            .param("date", LocalDate.now().toString()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByUserIdAndDate_returnsOk_whenAuthAsAdmin() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = validClient.getClientAppointments()
        .stream()
        .filter(a -> a.getStartDate().toLocalDate().equals(date))
        .count();

    mockMvc.perform(get("/api/v1/appointments/user/" + validClient.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByUserIdAndDate_returnsOk_whenAuthAsClient() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = validClient.getClientAppointments()
        .stream()
        .filter(a -> a.getStartDate().toLocalDate().equals(date))
        .count();

    mockMvc.perform(get("/api/v1/appointments/user/" + validClient.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByUserIdAndDate_returnsOk_whenAuthAsStaff() throws Exception {
    LocalDate date = LocalDate.now();
    long totalAppointments = validClient.getClientAppointments()
        .stream()
        .filter(a -> a.getStartDate().toLocalDate().equals(date))
        .count();

    mockMvc.perform(get("/api/v1/appointments/user/" + validClient.getId() + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(totalAppointments));
  }

  @Test
  void getAllByUserIdAndDate_returnsOkWithZeroTotalElements_whenInvalidUserId() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/user/" + INVALID_USER_ID + "/short")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("date", LocalDate.now().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getAllByUserIdAndDate_returnsCorrectlyMappedData_whenAuthAsAdmin() throws Exception {
    LocalDate date = LocalDate.now();
    String response = mockMvc.perform(
            get("/api/v1/appointments/user/" + validClient.getId() + "/short")
                .header("Authorization",
                    generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
                .param("date", date.toString()))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    List<ShortAppointmentView> result = objectMapper.readValue(response,
        new TypeReference<>() {
        });

    List<ShortAppointmentView> expectedAppointments =
        appointmentRepository.findAllByUserIdAndDate(validClient.getId(), date)
            .stream()
            .map(this::toShortAppointmentView)
            .toList();

    assertEquals(expectedAppointments, result);
  }

  @Test
  void getById_returnsNotFound_whenInvalidId() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/" + INVALID_APPOINTMENT_ID)
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isNotFound());
  }

  @Test
  void getById_returnsOkWithCorrectlyMappedData_whenValidId_whenAdminAuth() throws Exception {
    String response = mockMvc.perform(get("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    AppointmentView expectedAppointment = toAppointmentView(
        appointmentRepository.findById(validAppointment.getId())
            .orElseThrow(() -> new IllegalStateException("No appointment data in the database"))
    );

    assertEquals(expectedAppointment, result);
  }

  @Test
  void getById_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/appointments/" + validAppointment.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getById_returnsOk_whenAuthAsAdmin() throws Exception {
    Appointment appointment = appointmentRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No appointment data in the database"));

    String response = mockMvc.perform(get("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
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
    Optional<Appointment> appointment = validStaff.getStaffAppointments()
        .stream().findFirst();

    if (appointment.isPresent()) {
      String response = mockMvc.perform(get("/api/v1/appointments/" + appointment.get().getId())
              .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
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
        .filter(a -> !a.getStaff().getId().equals(validStaff.getId()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No appointment data in the database"));

    mockMvc.perform(get("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getById_returnsOk_whenAuthAsClient_whenGetHisAppointments() throws Exception {
    Optional<Appointment> appointment = validClient.getClientAppointments()
        .stream().findFirst();

    if (appointment.isPresent()) {
      String response = mockMvc.perform(get("/api/v1/appointments/" + appointment.get().getId())
              .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
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
        .filter(a -> !a.getClient().getId().equals(validClient.getId()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No appointment data in the database"));

    mockMvc.perform(get("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(post("/api/v1/appointments")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validAppointmentCreateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsOk_whenAuthAsAdmin() throws Exception {
    String response = mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validAppointmentCreateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(validAppointmentCreateDto.serviceId(), result.service().id());
    assertEquals(validAppointmentCreateDto.clientEmail(), result.client().email());
    assertEquals(validAppointmentCreateDto.staffEmail(), result.staff().email());
    assertEquals(validAppointmentCreateDto.date(), result.startDate());
    assertEquals(2, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(NOT_APPROVED_SUBJECT));
    assertTrue(receivedMessages[1].getSubject().contains(NOT_APPROVED_SUBJECT));
  }

  @Test
  void create_returnsNotFound_whenAuthAsAdmin_whenStaffEmailDoesNotExists() throws Exception {
    String notExistingStaffEmail = "notExistingStaffEmail@flow.com";

    userRepository.findByEmail(notExistingStaffEmail)
        .ifPresent(userRepository::delete);

    AppointmentCreate appointmentCreate = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        notExistingStaffEmail,
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
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
        validAppointmentCreateDto.serviceId(),
        notExistingClientEmail,
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
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
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(appointmentCreate)))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_returnsOk_whenAuthAsClient_whenHeIsInTheBody() throws Exception {
    String response = mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validAppointmentCreateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(validAppointmentCreateDto.serviceId(), result.service().id());
    assertEquals(validAppointmentCreateDto.clientEmail(), result.client().email());
    assertEquals(validAppointmentCreateDto.staffEmail(), result.staff().email());
    assertEquals(validAppointmentCreateDto.date(), result.startDate());

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(2, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(NOT_APPROVED_SUBJECT));
    assertTrue(receivedMessages[1].getSubject().contains(NOT_APPROVED_SUBJECT));
  }

  @Test
  void create_returnsForbidden_whenAuthAsClient_whenHeIsNotInTheBody() throws Exception {
    User anotherExistingClient = userRepository.findAll()
        .stream()
        .filter(u -> u.getRole() == UserRoles.CLIENT)
        .filter(u -> !u.getEmail().equals(SeededClientUsers.CLIENT1.getEmail()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No other client data in the database"));

    AppointmentCreate appointmentCreate = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        anotherExistingClient.getEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(appointmentCreate)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsOk_whenAuthAsStaff_whenHeIsInTheBody() throws Exception {
    String response = mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validAppointmentCreateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    assertEquals(validAppointmentCreateDto.serviceId(), result.service().id());
    assertEquals(validAppointmentCreateDto.clientEmail(), result.client().email());
    assertEquals(validAppointmentCreateDto.staffEmail(), result.staff().email());
    assertEquals(validAppointmentCreateDto.date(), result.startDate());

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(2, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(NOT_APPROVED_SUBJECT));
    assertTrue(receivedMessages[1].getSubject().contains(NOT_APPROVED_SUBJECT));
  }

  @Test
  void create_returnsForbidden_whenAuthAsStaff_whenHeIsNotInTheBody() throws Exception {
    User anotherExistingStaff = userRepository.findAll()
        .stream()
        .filter(u -> u.getRole() == UserRoles.EMPLOYEE)
        .filter(u -> !u.getEmail().equals(SeededStaffUsers.STAFF1.getEmail()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No other client data in the database"));

    AppointmentCreate appointmentCreate = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        anotherExistingStaff.getEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(appointmentCreate)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsBadRequest_whenServiceIdIsInvalid() throws Exception {

    AppointmentCreate invalidDto = new AppointmentCreate(
        INVALID_SERVICE_ID,
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenClientEmailIsInvalid() throws Exception {
    List<String> invalidEmails = List.of("invalid-email", "");

    for (String invalidEmail : invalidEmails) {
      AppointmentCreate invalidDto = new AppointmentCreate(
          validAppointmentCreateDto.serviceId(),
          invalidEmail,
          validAppointmentCreateDto.staffEmail(),
          validAppointmentCreateDto.date()
      );

      mockMvc.perform(post("/api/v1/appointments")
              .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
              .contentType("application/json")
              .content(objectMapper.writeValueAsString(invalidDto)))
          .andExpect(status().isBadRequest());
    }

    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        null,
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffEmailIsInvalid() throws Exception {
    List<String> invalidEmails = List.of("invalid-email", "");

    for (String invalidEmail : invalidEmails) {
      AppointmentCreate invalidDto = new AppointmentCreate(
          validAppointmentCreateDto.serviceId(),
          validAppointmentCreateDto.clientEmail(),
          invalidEmail,
          validAppointmentCreateDto.date()
      );

      mockMvc.perform(post("/api/v1/appointments")
              .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
              .contentType("application/json")
              .content(objectMapper.writeValueAsString(invalidDto)))
          .andExpect(status().isBadRequest());
    }

    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        null,
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDateIsInvalid() throws Exception {
    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        LocalDateTime.now().minusDays(1)
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffEmailProvidedInsteadOfClient() throws Exception {
    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenClientEmailProvidedInsteadOfStaff() throws Exception {
    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffWorkingTimeDoesNotMatch() throws Exception {
    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        LocalDateTime.now()
            .plusYears(2)
            .withHour(validStaff.getStaffDetails().getEndWorkingHour().getHour())
            .withMinute(validStaff.getStaffDetails().getEndWorkingHour().getMinute())
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffAvailabilityIsFalse() throws Exception {
    validStaff.getStaffDetails().setIsAvailable(false);
    userRepository.save(validStaff);

    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenServiceAvailabilityIsFalse() throws Exception {
    Service service = serviceRepository.findById(validAppointmentCreateDto.serviceId())
        .orElseThrow();
    service.setAvailability(false);
    serviceRepository.save(service);

    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenStaffDoesNotContainService() throws Exception {
    validStaff.getServices().clear();
    userRepository.save(validStaff);

    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenWorkSpaceCapacityIsReached() throws Exception {
    Service service = serviceRepository.findById(validAppointmentCreateDto.serviceId())
        .orElseThrow();
    service.getWorkSpace().setAvailableSlots(0);
    serviceRepository.save(service);

    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenOverLappingAppointments() throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(validClient);
    appointment.setStaff(validStaff);
    appointment.setService(validService);
    appointment.setStartDate(validAppointmentCreateDto.date());
    appointment.setEndDate(
        validAppointmentCreateDto.date().plusMinutes(validService.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(appointment);

    AppointmentCreate invalidDto = new AppointmentCreate(
        validAppointmentCreateDto.serviceId(),
        validAppointmentCreateDto.clientEmail(),
        validAppointmentCreateDto.staffEmail(),
        validAppointmentCreateDto.date()
    );

    mockMvc.perform(post("/api/v1/appointments")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsForbidden_whenNoAuth() throws Exception {
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromNotApprovedToApproved() throws Exception {
    validAppointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(updateDto.status().name(), result.status().name());
    assertEquals(2, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(APPROVED_SUBJECT));
    assertTrue(receivedMessages[1].getSubject().contains(APPROVED_SUBJECT));
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromNotApprovedToCompleted() throws Exception {
    validAppointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
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
    validAppointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(updateDto.status().name(), result.status().name());
    assertEquals(1, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(CANCELED_SUBJECT));
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromApprovedToCanceled() throws Exception {
    validAppointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(updateDto.status().name(), result.status().name());
    assertEquals(1, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(CANCELED_SUBJECT));
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromApprovedToCompleted() throws Exception {
    validAppointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
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
    validAppointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(updateDto.status().name(), result.status().name());
    assertEquals(0, receivedMessages.length);
  }

  @Test
  void update_returnsOk_whenValidAuth_whenUpdatingFromCompletedToCanceled() throws Exception {
    validAppointment.setStatus(AppointmentStatus.COMPLETED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(updateDto.status().name(), result.status().name());
    assertEquals(1, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(CANCELED_SUBJECT));
  }

  @Test
  void update_returnsBadRequest_whenValidAuth_whenUpdatingFromCompletedToApproved()
      throws Exception {
    validAppointment.setStatus(AppointmentStatus.COMPLETED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenValidAuth_whenUpdatingFromCanceledToApproved()
      throws Exception {
    validAppointment.setStatus(AppointmentStatus.CANCELED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenValidAuth_whenUpdatingFromCanceledToCanceled()
      throws Exception {
    validAppointment.setStatus(AppointmentStatus.CANCELED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenValidAuth_whenUpdatingFromCanceledToCompleted()
      throws Exception {
    validAppointment.setStatus(AppointmentStatus.CANCELED);
    appointmentRepository.save(validAppointment);
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsOk_whenAuthAsClient_whenUpdatingOwnAppointment_whenStatusCanceled()
      throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(validClient);
    appointment.setStaff(validStaff);
    appointment.setService(validService);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(validService.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(appointment);

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(updateDto.status().name(), result.status().name());
    assertEquals(1, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(CANCELED_SUBJECT));
  }

  @Test
  void update_returnsBadRequest_whenAuthAsClient_whenUpdatingOwnAppointment_whenStatusApproved()
      throws Exception {
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenAuthAsClient_whenUpdatingOwnAppointment_whenStatusCompleted()
      throws Exception {
    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    mockMvc.perform(put("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsForbidden_whenAuthAsClient_whenUpdatingOthersAppointments_whenStatusCanceled()
      throws Exception {
    Appointment appointment = appointmentRepository.findAll()
        .stream()
        .filter(a -> !a.getClient().getId().equals(validClient.getId()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No other client appointments in the database")
        );

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenAuthAsStaff_whenUpdatingOwnAppointment_whenStatusApproved()
      throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(validClient);
    appointment.setStaff(validStaff);
    appointment.setService(validService);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(validService.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(appointment);

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.APPROVED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(updateDto.status().name(), result.status().name());
    assertEquals(2, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(APPROVED_SUBJECT));
    assertTrue(receivedMessages[1].getSubject().contains(APPROVED_SUBJECT));
  }

  @Test
  void update_returnsOk_whenAuthAsStaff_whenUpdatingOwnAppointment_whenStatusCanceled()
      throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(validClient);
    appointment.setStaff(validStaff);
    appointment.setService(validService);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(validService.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(appointment);

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AppointmentView result = objectMapper.readValue(response, AppointmentView.class);
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertEquals(updateDto.status().name(), result.status().name());
    assertEquals(1, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(CANCELED_SUBJECT));
  }

  @Test
  void update_returnsOk_whenAuthAsStaff_whenUpdatingOwnAppointment_whenStatusCompleted()
      throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(validClient);
    appointment.setStaff(validStaff);
    appointment.setService(validService);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(validService.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.APPROVED);
    appointmentRepository.save(appointment);

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.CANCELED);

    String response = mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
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
        .filter(a -> !a.getStaff().getId().equals(validStaff.getId()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No other client appointments in the database")
        );

    AppointmentUpdate updateDto = new AppointmentUpdate(UpdateAppointmentStatus.COMPLETED);

    mockMvc.perform(put("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(delete("/api/v1/appointments/" + validAppointment.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNoContent_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(delete("/api/v1/appointments/" + validAppointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isNoContent());

    assertFalse(appointmentRepository.existsById(validAppointment.getId()));
  }

  @Test
  void delete_returnsNoContent_whenAuthAsClient_whenDeletingOwnAppointment() throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(validClient);
    appointment.setStaff(validStaff);
    appointment.setService(validService);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(validService.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(appointment);

    mockMvc.perform(delete("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isNoContent());

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertFalse(appointmentRepository.existsById(appointment.getId()));
    assertEquals(1, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(CANCELED_SUBJECT));
  }

  @Test
  void delete_returnsForbidden_whenAuthAsClient_whenDeletingOthersAppointment() throws Exception {
    Appointment appointment = appointmentRepository.findAll()
        .stream()
        .filter(a -> !a.getClient().getId().equals(validClient.getId()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No other client appointments in the database"));

    mockMvc.perform(delete("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNoContent_whenAuthAsStaff_whenDeletingOwnAppointment() throws Exception {
    Appointment appointment = new Appointment();
    appointment.setClient(validClient);
    appointment.setStaff(validStaff);
    appointment.setService(validService);
    appointment.setStartDate(LocalDateTime.now().plusYears(10));
    appointment.setEndDate(
        LocalDateTime.now().plusYears(10).plusMinutes(validService.getDuration().toMinutes()));
    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
    appointmentRepository.save(appointment);

    mockMvc.perform(delete("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isNoContent());

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertFalse(appointmentRepository.existsById(appointment.getId()));
    assertEquals(1, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(CANCELED_SUBJECT));
  }

  @Test
  void delete_returnsForbidden_whenAuthAsStaff_whenDeletingOthersAppointment() throws Exception {
    Appointment appointment = appointmentRepository.findAll()
        .stream()
        .filter(a -> !a.getStaff().getId().equals(validStaff.getId()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No other staff appointments in the database"));

    mockMvc.perform(delete("/api/v1/appointments/" + appointment.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNotFound_whenAppointmentDoesNotExist() throws Exception {
    mockMvc.perform(delete("/api/v1/appointments/" + INVALID_APPOINTMENT_ID)
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
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