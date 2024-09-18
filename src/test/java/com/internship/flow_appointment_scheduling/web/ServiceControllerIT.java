package com.internship.flow_appointment_scheduling.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.internship.flow_appointment_scheduling.FlowAppointmentSchedulingApplication;
import com.internship.flow_appointment_scheduling.config.TestContainersConfig;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.features.work_space.entity.WorkSpace;
import com.internship.flow_appointment_scheduling.features.work_space.repository.WorkSpaceRepository;
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtService;
import com.internship.flow_appointment_scheduling.seed.enums.SeededAdminUsers;
import com.internship.flow_appointment_scheduling.seed.enums.SeededClientUsers;
import com.internship.flow_appointment_scheduling.seed.enums.SeededStaffUsers;
import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
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
class ServiceControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ServiceRepository serviceRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private WorkSpaceRepository workSpaceRepository;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private ObjectMapper objectMapper;

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "duke"))
      .withPerMethodLifecycle(true);

  private static Service validService;
  private static WorkSpace validWorkSpace;
  private static final long INVALID_SERVICE_ID = -1;
  private static ServiceDTO validServiceDto;
  private static final String VALID_STAFF_EMAIL = SeededStaffUsers.STAFF1.getEmail();
  private static final String INVALID_STAFF_EMAIL = "invalidEmail@test.bg";
  private static final String VALID_CLIENT_EMAIL = SeededClientUsers.CLIENT1.getEmail();
  private static final String CANCELED_SUBJECT = "Appointment Canceled";

  @BeforeEach
  void setUp() {
    validService = serviceRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No service data in the database"));

    validWorkSpace = workSpaceRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No work space data in the database"));

    validServiceDto =
        new ServiceDTO("Service Name",
            "Service Description",
            true,
            BigDecimal.valueOf(100),
            Duration.ofMinutes(30),
            validWorkSpace.getName()
        );

    if (userRepository.findByEmail(INVALID_STAFF_EMAIL).isPresent()) {
      userRepository.delete(userRepository.findByEmail(INVALID_STAFF_EMAIL).get());
    }
  }

  @Test
  void getAll_returnsOk_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/services"))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsOk_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsOk_whenUsingStaffEmailParam() throws Exception {
    String staffEmail = SeededStaffUsers.STAFF1.getEmail();

    String jsonResponse = mockMvc.perform(get("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", staffEmail))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    List<Integer> serviceIds = JsonPath.read(jsonResponse, "$.content[*].id");

    for (Integer serviceId : serviceIds) {
      boolean isAssigned = serviceRepository.findById(serviceId.longValue())
          .map(service -> service.getUsers().stream()
              .anyMatch(user -> user.getEmail().equals(staffEmail)))
          .orElse(false);

      assertTrue(isAssigned);
    }
  }

  @Test
  void getById_returnsOk_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + validService.getId()))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsOk_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsNotFound_whenInvalidId() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + INVALID_SERVICE_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  void getById_returnsServiceData_whenAuthAsAdmin() throws Exception {
    String jsonResponse = mockMvc.perform(get("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    ServiceView serviceView = objectMapper.readValue(jsonResponse, ServiceView.class);
    Service service = serviceRepository.findById(validService.getId()).orElseThrow();

    assertEquals(service.getId(), serviceView.id());
    assertEquals(service.getName(), serviceView.name());
    assertEquals(service.getDescription(), serviceView.description());
    assertEquals(service.getDuration(), serviceView.duration());
    assertEquals(service.getAvailability(), serviceView.availability());
    assertEquals(service.getPrice().doubleValue(), serviceView.price());
    assertEquals(service.getWorkSpace().getName(), serviceView.workSpace().name());
  }

  @Test
  void getAllWorkSpacesNames_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/services/workspaces"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllWorkSpacesNames_returnsOk_whenAuthAsAdmin() throws Exception {
    String jsonResponse = mockMvc.perform(get("/api/v1/services/workspaces")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    List<String> returnedWorkSpaceNames = objectMapper.readValue(jsonResponse,
        new TypeReference<>() {
        });
    List<String> expectedWorkSpaceNames = workSpaceRepository.findAll()
        .stream()
        .map(WorkSpace::getName)
        .toList();

    assertEquals(expectedWorkSpaceNames.size(), returnedWorkSpaceNames.size());
    assertTrue(returnedWorkSpaceNames.containsAll(expectedWorkSpaceNames));
  }

  @Test
  void getAllWorkSpacesNames_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/services/workspaces")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllWorkSpacesNames_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/services/workspaces")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isOk());

    Service service = serviceRepository.findAll().stream()
        .filter(s -> s.getName().equals(validServiceDto.name()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Service not created"));

    assertEquals(validServiceDto.name(), service.getName());
    assertEquals(validServiceDto.description(), service.getDescription());
    assertEquals(validServiceDto.availability(), service.getAvailability());
    assertEquals(validServiceDto.price(), service.getPrice());
    assertEquals(validServiceDto.duration(), service.getDuration());
    assertEquals(validWorkSpace.getName(), service.getWorkSpace().getName());
  }

  @Test
  void create_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsBadRequest_whenNameIsInvalid() throws Exception {

    List<String> invalidNames = List.of("", "Jo", "".repeat(255));

    for (String invalidName : invalidNames) {
      ServiceDTO invalidDto = new ServiceDTO(
          invalidName,
          validServiceDto.description(),
          validServiceDto.availability(),
          validServiceDto.price(),
          validServiceDto.duration(),
          validServiceDto.workSpaceName()
      );

      mockMvc.perform(post("/api/v1/services")
              .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
              .contentType("application/json")
              .content(objectMapper.writeValueAsString(invalidDto)))
          .andExpect(status().isBadRequest());
    }

    ServiceDTO invalidDto = new ServiceDTO(
        null,
        validServiceDto.description(),
        validServiceDto.availability(),
        validServiceDto.price(),
        validServiceDto.duration(),
        validServiceDto.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDescriptionIsBlank() throws Exception {

    ServiceDTO invalidDto = new ServiceDTO(
        validServiceDto.name(),
        "",
        validServiceDto.availability(),
        validServiceDto.price(),
        validServiceDto.duration(),
        validServiceDto.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenAvailabilityIsNull() throws Exception {
    ServiceDTO invalidDto = new ServiceDTO(
        validServiceDto.name(),
        validServiceDto.description(),
        null,
        validServiceDto.price(),
        validServiceDto.duration(),
        validServiceDto.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenPriceIsInvalid() throws Exception {
    ServiceDTO invalidDto = new ServiceDTO(
        validServiceDto.name(),
        validServiceDto.description(),
        validServiceDto.availability(),
        null,
        validServiceDto.duration(),
        validServiceDto.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    invalidDto = new ServiceDTO(
        validServiceDto.name(),
        validServiceDto.description(),
        validServiceDto.availability(),
        BigDecimal.valueOf(-1),
        validServiceDto.duration(),
        validServiceDto.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDurationIsInvalid() throws Exception {
    ServiceDTO invalidDto = new ServiceDTO(
        validServiceDto.name(),
        validServiceDto.description(),
        validServiceDto.availability(),
        validServiceDto.price(),
        null,
        validServiceDto.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    invalidDto = new ServiceDTO(
        validServiceDto.name(),
        validServiceDto.description(),
        validServiceDto.availability(),
        validServiceDto.price(),
        Duration.ofMinutes(-1),
        validServiceDto.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenWorkSpaceNameIsInvalid() throws Exception {
    String invalidWorkSpaceName = "Invalid Work Space Name";
    workSpaceRepository.findByName(invalidWorkSpaceName)
        .ifPresent(workSpaceRepository::delete);
    ServiceDTO invalidDto = new ServiceDTO(
        validServiceDto.name(),
        validServiceDto.description(),
        validServiceDto.availability(),
        validServiceDto.price(),
        validServiceDto.duration(),
        ""
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    invalidDto = new ServiceDTO(
        validServiceDto.name(),
        validServiceDto.description(),
        validServiceDto.availability(),
        validServiceDto.price(),
        validServiceDto.duration(),
        invalidWorkSpaceName
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + validService.getId() + "/assign")
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void assignStaff_returnsOk_whenAuthAsAdminAndStaffNotExistingInTheService() throws Exception {
    Service service = serviceRepository.findById(validService.getId()).orElseThrow();
    service.getUsers().clear();
    serviceRepository.save(service);

    mockMvc.perform(post("/api/v1/services/" + validService.getId() + "/assign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isOk());

    service = serviceRepository.findById(validService.getId()).orElseThrow();
    assertTrue(service.getUsers().stream()
        .anyMatch(user -> user.getEmail().equals(VALID_STAFF_EMAIL)));
  }

  @Test
  void assignStaff_returnsBadRequest_whenAuthAsAdminAndStaffAlreadyExistsInTheService()
      throws Exception {
    Service service = serviceRepository.findById(validService.getId()).orElseThrow();
    User userToAdd = userRepository.findByEmail(VALID_STAFF_EMAIL).orElseThrow();
    if (!service.getUsers().contains(userToAdd)) {
      service.getUsers().add(userToAdd);
    }
    serviceRepository.save(service);

    mockMvc.perform(post("/api/v1/services/" + validService.getId() + "/assign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + validService.getId() + "/assign")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void assignStaff_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + validService.getId() + "/assign")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void assignStaff_returnsNotFound_whenInvalidServiceId() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + INVALID_SERVICE_ID + "/assign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isNotFound());
  }

  @Test
  void assignStaff_returnsBadRequest_whenProvidedClientEmailInsteadOfStaff() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + validService.getId() + "/assign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", VALID_CLIENT_EMAIL))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsBadRequest_whenProvidedNotExistingEmail() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + validService.getId() + "/assign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", VALID_CLIENT_EMAIL))
        .andExpect(status().isBadRequest());
  }

  @Test
  void unassignStaff_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId() + "/unassign")
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void unassignStaff_returnsOk_whenAuthAsAdminAndUserHasTheService() throws Exception {
    Service service = serviceRepository.findById(validService.getId()).orElseThrow();
    User user = userRepository.findByEmail(VALID_STAFF_EMAIL).orElseThrow();
    if (!service.getUsers().contains(user)) {
      service.getUsers().add(user);
      serviceRepository.save(service);
    }
    long appointmentsToBeCanceled = user.getStaffAppointments()
        .stream()
        .filter(a -> a.getService().getId().equals(validService.getId()))
        .filter(a -> a.getStatus() == AppointmentStatus.APPROVED ||
            a.getStatus() == AppointmentStatus.NOT_APPROVED)
        .count();

    mockMvc.perform(put("/api/v1/services/" + validService.getId() + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isOk());

    service = serviceRepository.findById(validService.getId()).orElseThrow();
    assertFalse(service.getUsers().contains(user));

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(appointmentsToBeCanceled, receivedMessages.length);
  }

  @Test
  void unassignStaff_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId() + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void unassignStaff_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId() + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void unassignStaff_returnsNotFound_whenInvalidServiceId() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + INVALID_SERVICE_ID + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isNotFound());
  }

  @Test
  void unassignStaff_returnsNotFound_whenNotExistingStaffEmail() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId() + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", INVALID_STAFF_EMAIL))
        .andExpect(status().isNotFound());
  }

  @Test
  void unassignStaff_returnsBadRequest_whenValidEmailButNotStaffIsProvided() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId() + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .param("staffEmail", VALID_CLIENT_EMAIL))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isOk());

    Service service = serviceRepository.findById(validService.getId()).orElseThrow();
    assertEquals(validServiceDto.name(), service.getName());
    assertEquals(validServiceDto.description(), service.getDescription());
    assertEquals(validServiceDto.availability(), service.getAvailability());
    assertEquals(validServiceDto.price(), service.getPrice());
    assertEquals(validServiceDto.duration(), service.getDuration());
    assertEquals(validWorkSpace.getName(), service.getWorkSpace().getName());
  }

  @Test
  void update_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsNotFound_whenInvalidServiceId() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + INVALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validServiceDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_returnsOk_andCancelsAppointments_whenAvailabilityChangesToFalse() throws Exception {
    Service service = serviceRepository.findById(validService.getId()).orElseThrow();
    service.setAvailability(true);
    serviceRepository.save(service);

    long appointmentsToBeCanceled = service.getAppointments()
        .stream()
        .filter(a -> a.getStatus() == AppointmentStatus.APPROVED ||
            a.getStatus() == AppointmentStatus.NOT_APPROVED)
        .count();

    ServiceDTO updateDto = new ServiceDTO(
        validServiceDto.name(),
        validServiceDto.description(),
        false,
        validServiceDto.price(),
        validServiceDto.duration(),
        validServiceDto.workSpaceName()
    );

    mockMvc.perform(put("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk());

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(appointmentsToBeCanceled, receivedMessages.length);
  }

  @Test
  void delete_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(delete("/api/v1/services/" + validService.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(delete("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededStaffUsers.STAFF1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(delete("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededClientUsers.CLIENT1.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNotFound_whenInvalidServiceId() throws Exception {
    mockMvc.perform(delete("/api/v1/services/" + INVALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_returnsNoContent_whenAuthAsAdmin() throws Exception {
    long totalAppointmentsToBeCanceled = serviceRepository.findById(validService.getId())
        .map(Service::getAppointments)
        .stream()
        .flatMap(List::stream)
        .filter(a -> a.getStatus() == AppointmentStatus.APPROVED ||
            a.getStatus() == AppointmentStatus.NOT_APPROVED)
        .count();

    mockMvc.perform(delete("/api/v1/services/" + validService.getId())
            .header("Authorization", generateBarrierAuthHeader(SeededAdminUsers.ADMIN1.getEmail())))
        .andExpect(status().isNoContent());

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

    assertFalse(serviceRepository.findById(validService.getId()).isPresent());
    assertEquals(totalAppointmentsToBeCanceled, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains(CANCELED_SUBJECT));
  }

  private String generateBarrierAuthHeader(String userEmail) {
    String token = jwtService.generateToken(userEmail).jwtToken().token();
    return "Bearer " + token;
  }
}