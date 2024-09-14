package com.internship.flow_appointment_scheduling.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.flow_appointment_scheduling.FlowAppointmentSchedulingApplication;
import com.internship.flow_appointment_scheduling.config.TestContainersConfig;
import com.internship.flow_appointment_scheduling.enums.Users;
import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.features.work_space.entity.WorkSpace;
import com.internship.flow_appointment_scheduling.features.work_space.repository.WorkSpaceRepository;
import com.internship.flow_appointment_scheduling.infrastructure.mail_service.MailService;
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtService;
import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
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
  @MockBean
  private MailService mailService;

  private static long VALID_SERVICE_ID;
  private static WorkSpace VALID_WORK_SPACE;
  private static final long INVALID_SERVICE_ID = -1;
  private static ServiceDTO VALID_SERVICE_DTO;
  private static String VALID_STAFF_EMAIL;
  private static final String INVALID_STAFF_EMAIL = "invalidEmail@test.bg";
  private static String VALID_CLIENT_EMAIL;

  @BeforeEach
  void setUp() {
    VALID_SERVICE_ID = serviceRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No service data in the database"))
        .getId();

    VALID_WORK_SPACE = workSpaceRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No work space data in the database"));

    VALID_SERVICE_DTO =
        new ServiceDTO("Service Name",
            "Service Description",
            true,
            BigDecimal.valueOf(100),
            Duration.ofMinutes(30),
            VALID_WORK_SPACE.getName()
        );

    VALID_STAFF_EMAIL = userRepository.findAll().stream()
        .filter(user -> user.getRole().equals(UserRoles.EMPLOYEE))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No staff data in the database"))
        .getEmail();

    VALID_CLIENT_EMAIL = userRepository.findAll().stream()
        .filter(user -> user.getRole().equals(UserRoles.CLIENT))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No client data in the database"))
        .getEmail();

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
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsOk_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsOk_whenUsingStaffEmailParam() throws Exception {
    String staffEmail = Users.STAFF.getEmail();

    String jsonResponse = mockMvc.perform(get("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
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
    mockMvc.perform(get("/api/v1/services/" + VALID_SERVICE_ID))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsOk_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsNotFound_whenInvalidId() throws Exception {
    mockMvc.perform(get("/api/v1/services/" + INVALID_SERVICE_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  void getById_returnsServiceData_whenAuthAsAdmin() throws Exception {
    String jsonResponse = mockMvc.perform(get("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    ServiceView serviceView = objectMapper.readValue(jsonResponse, ServiceView.class);
    Service service = serviceRepository.findById(VALID_SERVICE_ID).orElseThrow();

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
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
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
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllWorkSpacesNames_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/services/workspaces")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isOk());

    Service service = serviceRepository.findAll().stream()
        .filter(s -> s.getName().equals(VALID_SERVICE_DTO.name()))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Service not created"));

    assertEquals(VALID_SERVICE_DTO.name(), service.getName());
    assertEquals(VALID_SERVICE_DTO.description(), service.getDescription());
    assertEquals(VALID_SERVICE_DTO.availability(), service.getAvailability());
    assertEquals(VALID_SERVICE_DTO.price(), service.getPrice());
    assertEquals(VALID_SERVICE_DTO.duration(), service.getDuration());
    assertEquals(VALID_WORK_SPACE.getName(), service.getWorkSpace().getName());
  }

  @Test
  void create_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  void create_returnsBadRequest_whenNameIsInvalid() throws Exception {

    List<String> invalidNames = List.of("", "Jo", "".repeat(255));

    for (String invalidName : invalidNames) {
      ServiceDTO invalidDto = new ServiceDTO(
          invalidName,
          VALID_SERVICE_DTO.description(),
          VALID_SERVICE_DTO.availability(),
          VALID_SERVICE_DTO.price(),
          VALID_SERVICE_DTO.duration(),
          VALID_SERVICE_DTO.workSpaceName()
      );

      mockMvc.perform(post("/api/v1/services")
              .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
              .contentType("application/json")
              .content(objectMapper.writeValueAsString(invalidDto)))
          .andExpect(status().isBadRequest());
    }

    ServiceDTO invalidDto = new ServiceDTO(
        null,
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDescriptionIsBlank() throws Exception {

    ServiceDTO invalidDto = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        "",
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenAvailabilityIsNull() throws Exception {
    ServiceDTO invalidDto = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        null,
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenPriceIsInvalid() throws Exception {
    ServiceDTO invalidDto = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        null,
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    invalidDto = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        BigDecimal.valueOf(-1),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDurationIsInvalid() throws Exception {
    ServiceDTO invalidDto = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        null,
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    invalidDto = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        Duration.ofMinutes(-1),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
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
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        ""
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());

    invalidDto = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        invalidWorkSpaceName
    );

    mockMvc.perform(post("/api/v1/services")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + VALID_SERVICE_ID + "/assign")
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void assignStaff_returnsOk_whenAuthAsAdminAndStaffNotExistingInTheService() throws Exception {
    Service service = serviceRepository.findById(VALID_SERVICE_ID).orElseThrow();
    service.getUsers().clear();
    serviceRepository.save(service);

    mockMvc.perform(post("/api/v1/services/" + VALID_SERVICE_ID + "/assign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isOk());

    service = serviceRepository.findById(VALID_SERVICE_ID).orElseThrow();
    assertTrue(service.getUsers().stream()
        .anyMatch(user -> user.getEmail().equals(VALID_STAFF_EMAIL)));
  }

  @Test
  void assignStaff_returnsBadRequest_whenAuthAsAdminAndStaffAlreadyExistsInTheService()
      throws Exception {
    Service service = serviceRepository.findById(VALID_SERVICE_ID).orElseThrow();
    User userToAdd = userRepository.findByEmail(VALID_STAFF_EMAIL).orElseThrow();
    if (!service.getUsers().contains(userToAdd)) {
      service.getUsers().add(userToAdd);
    }
    serviceRepository.save(service);

    mockMvc.perform(post("/api/v1/services/" + VALID_SERVICE_ID + "/assign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + VALID_SERVICE_ID + "/assign")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void assignStaff_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + VALID_SERVICE_ID + "/assign")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void assignStaff_returnsNotFound_whenInvalidServiceId() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + INVALID_SERVICE_ID + "/assign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isNotFound());
  }

  @Test
  void assignStaff_returnsBadRequest_whenProvidedClientEmailInsteadOfStaff() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + VALID_SERVICE_ID + "/assign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", VALID_CLIENT_EMAIL))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsBadRequest_whenProvidedNotExistingEmail() throws Exception {
    mockMvc.perform(post("/api/v1/services/" + VALID_SERVICE_ID + "/assign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", VALID_CLIENT_EMAIL))
        .andExpect(status().isBadRequest());
  }

  @Test
  void unassignStaff_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID + "/unassign")
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void unassignStaff_returnsOk_whenAuthAsAdminAndUserHasTheService() throws Exception {
    Service service = serviceRepository.findById(VALID_SERVICE_ID).orElseThrow();
    User user = userRepository.findByEmail(VALID_STAFF_EMAIL).orElseThrow();
    if (!service.getUsers().contains(user)) {
      service.getUsers().add(user);
      serviceRepository.save(service);
    }
    boolean isMailServiceBeingCalled = user.getStaffAppointments()
        .stream()
        .filter(a -> a.getService().getId().equals(VALID_SERVICE_ID))
        .anyMatch(a -> a.getStatus() == AppointmentStatus.APPROVED ||
            a.getStatus() == AppointmentStatus.NOT_APPROVED);

    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isOk());

    service = serviceRepository.findById(VALID_SERVICE_ID).orElseThrow();
    assertFalse(service.getUsers().contains(user));
    if (isMailServiceBeingCalled) {
      verify(mailService,
          atLeastOnce()).sendCanceledAppointmentNotificationToClient(any(Appointment.class));
    }
  }

  @Test
  void unassignStaff_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void unassignStaff_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isForbidden());
  }

  @Test
  void unassignStaff_returnsNotFound_whenInvalidServiceId() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + INVALID_SERVICE_ID + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isNotFound());
  }

  @Test
  void unassignStaff_returnsNotFound_whenNotExistingStaffEmail() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", INVALID_STAFF_EMAIL))
        .andExpect(status().isNotFound());
  }

  @Test
  void unassignStaff_returnsBadRequest_whenValidEmailButNotStaffIsProvided() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID + "/unassign")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("staffEmail", VALID_CLIENT_EMAIL))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isOk());

    Service service = serviceRepository.findById(VALID_SERVICE_ID).orElseThrow();
    assertEquals(VALID_SERVICE_DTO.name(), service.getName());
    assertEquals(VALID_SERVICE_DTO.description(), service.getDescription());
    assertEquals(VALID_SERVICE_DTO.availability(), service.getAvailability());
    assertEquals(VALID_SERVICE_DTO.price(), service.getPrice());
    assertEquals(VALID_SERVICE_DTO.duration(), service.getDuration());
    assertEquals(VALID_WORK_SPACE.getName(), service.getWorkSpace().getName());
  }

  @Test
  void update_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsNotFound_whenInvalidServiceId() throws Exception {
    mockMvc.perform(put("/api/v1/services/" + INVALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_returnsOk_andCancelsAppointments_whenAvailabilityChangesToFalse() throws Exception {
    Service service = serviceRepository.findById(VALID_SERVICE_ID).orElseThrow();
    service.setAvailability(true);
    serviceRepository.save(service);
    boolean isMailServiceBeingCalled = service.getAppointments()
        .stream()
        .anyMatch(a -> a.getStatus() == AppointmentStatus.APPROVED ||
            a.getStatus() == AppointmentStatus.NOT_APPROVED);

    ServiceDTO updateDto = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        false,
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(put("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk());

    if (isMailServiceBeingCalled) {
      verify(mailService, atLeastOnce()).sendCanceledAppointmentNotificationToClient(
          any(Appointment.class));
    }
  }

  @Test
  void delete_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(delete("/api/v1/services/" + VALID_SERVICE_ID))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(delete("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(delete("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNotFound_whenInvalidServiceId() throws Exception {
    mockMvc.perform(delete("/api/v1/services/" + INVALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_returnsNoContent_whenAuthAsAdmin() throws Exception {
    boolean isMailServiceBeingCalled = serviceRepository.findById(VALID_SERVICE_ID)
        .map(Service::getAppointments)
        .stream()
        .flatMap(List::stream)
        .anyMatch(a -> a.getStatus() == AppointmentStatus.APPROVED ||
            a.getStatus() == AppointmentStatus.NOT_APPROVED);

    mockMvc.perform(delete("/api/v1/services/" + VALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isNoContent());

    assertFalse(serviceRepository.findById(VALID_SERVICE_ID).isPresent());
    if (isMailServiceBeingCalled) {
      verify(mailService, atLeastOnce()).sendCanceledAppointmentNotificationToClient(
          any(Appointment.class));
    }
  }

  private String generateBarrierAuthHeader(String userEmail) {
    String token = jwtService.generateToken(userEmail).jwtToken().token();
    return "Bearer " + token;
  }
}