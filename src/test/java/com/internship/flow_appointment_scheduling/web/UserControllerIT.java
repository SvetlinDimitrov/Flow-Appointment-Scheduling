package com.internship.flow_appointment_scheduling.web;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.flow_appointment_scheduling.FlowAppointmentSchedulingApplication;
import com.internship.flow_appointment_scheduling.config.TestContainersConfig;
import com.internship.flow_appointment_scheduling.enums.Users;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffDetailsDto;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffDetailsView;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffHireDto;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffModifyDto;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPasswordUpdate;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.mail_service.MailService;
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtService;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
@ContextConfiguration(classes = {TestContainersConfig.class,
    FlowAppointmentSchedulingApplication.class})
@AutoConfigureMockMvc
class UserControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ServiceRepository serviceRepository;
  @Autowired
  private JwtService jwtService;
  @MockBean
  private MailService mailService;
  @Autowired
  private ObjectMapper objectMapper;

  private static Service VALID_SERVICE;
  private static long ADMIN_ID;
  private static User VALID_STAFF;
  private static User VALID_CLIENT;
  private static final long INVALID_SERVICE_ID = -1L;
  private static final long INVALID_USER_ID = -1L;
  private static final UserPostRequest VALID_USER_POST_REQUEST = new UserPostRequest(
      "ValidPassword1!",
      "valid.email@example.com",
      "John",
      "Doe"
  );
  private static final UserPutRequest VALID_USER_PUT_REQUEST = new UserPutRequest(
      "NewFirstName",
      "NewLastName"
  );
  private static final StaffDetailsDto VALID_STAFF_DETAILS_DTO = new StaffDetailsDto(
      1000.0,
      LocalTime.of(9, 0),
      LocalTime.of(17, 0)
  );
  private static final StaffModifyDto VALID_STAFF_MODIFY_DTO = new StaffModifyDto(
      UserRoles.EMPLOYEE,
      1000.0,
      true,
      LocalTime.of(9, 0),
      LocalTime.of(17, 0)
  );
  private static final UserPasswordUpdate VALID_USER_PASSWORD_UPDATE = new UserPasswordUpdate(
      "password123A!"
  );


  @BeforeEach
  void setUp() {
    VALID_SERVICE = serviceRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No service data in the database"));

    ADMIN_ID = userRepository.findByEmail(Users.ADMIN.getEmail())
        .orElseThrow(() -> new IllegalStateException("Admin user not found in the database"))
        .getId();

    VALID_STAFF = userRepository.findByEmail(Users.STAFF.getEmail())
        .orElseThrow(() -> new IllegalStateException("Staff user not found in the database"));

    VALID_CLIENT = userRepository.findByEmail(Users.CLIENT.getEmail())
        .orElseThrow(() -> new IllegalStateException("Client user not found in the database"));
  }

  @Test
  void getAll_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/users"))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAll_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/users")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAll_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/users")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/users")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAll_returnsAllUsers_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/users")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isNotEmpty());
  }

  @Test
  void getAll_returnsUsersWithSpecificRole_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/users")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .param("userRole", UserRoles.CLIENT.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isNotEmpty())
        .andExpect(jsonPath("$.content[*].role")
            .value(everyItem(equalTo(UserRoles.CLIENT.name())))
        );
  }

  @Test
  void getAllByServiceId_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/users/service/" + VALID_SERVICE.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getAllByServiceId_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/users/service/" + VALID_SERVICE.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAllByServiceId_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/users/service/" + VALID_SERVICE.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAllByServiceId_returnsOk_whenAuthAsClient() throws Exception {
    mockMvc.perform(get("/api/v1/users/service/" + VALID_SERVICE.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getAllByServiceId_returnsOkEmptyPage_whenInvalidServiceId() throws Exception {
    mockMvc.perform(get("/api/v1/users/service/" + INVALID_SERVICE_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(get("/api/v1/users/" + ADMIN_ID))
        .andExpect(status().isForbidden());
  }

  @Test
  void getById_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/users/" + ADMIN_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(get("/api/v1/users/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsForbidden_whenAuthAsClientAccessingAnotherUser() throws Exception {
    mockMvc.perform(get("/api/v1/users/" + ADMIN_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getById_returnsOk_whenAuthAsClientAccessingSelf() throws Exception {
    mockMvc.perform(get("/api/v1/users/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returnsNotFound_whenUserNotFound() throws Exception {
    mockMvc.perform(get("/api/v1/users/" + INVALID_USER_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isNotFound());
  }

  @Test
  void getById_returnsStaffDetails_whenReturnStaff() throws Exception {
    User user = userRepository.findById(VALID_STAFF.getId()).orElseThrow();
    UserView expectedUserView = generateUserView(user);

    MvcResult result = mockMvc.perform(get("/api/v1/users/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    UserView actualUserView = objectMapper.readValue(jsonResponse, UserView.class);

    assertEquals(expectedUserView, actualUserView);
  }

  @Test
  void getById_returnsNullStaffDetails_whenReturnClient() throws Exception {
    User user = userRepository.findById(VALID_CLIENT.getId()).orElseThrow();
    UserView expectedUserView = generateUserView(user);

    MvcResult result = mockMvc.perform(get("/api/v1/users/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    UserView actualUserView = objectMapper.readValue(jsonResponse, UserView.class);

    assertEquals(expectedUserView, actualUserView);
  }

  @Test
  void create_returnsCreated_whenValidRequest() throws Exception {
    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_POST_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("valid.email@example.com"));
  }

  @Test
  void create_returnsBadRequest_whenInvalidPassword() throws Exception {
    List<String> invalidPasswords = List.of(
        "password", "PASSWORD", "Password1", "Password!", "", "Jo"
    );

    for (String password : invalidPasswords) {
      UserPostRequest request = new UserPostRequest(
          password,
          VALID_USER_POST_REQUEST.email(),
          VALID_USER_POST_REQUEST.firstName(),
          VALID_USER_POST_REQUEST.lastName()
      );
      mockMvc.perform(post("/api/v1/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  void create_returnsBadRequest_whenInvalidEmail() throws Exception {
    List<String> invalidEmail = List.of(
        "", "   ", "  11", "email@", Users.ADMIN.getEmail()
    );

    for (String email : invalidEmail) {
      UserPostRequest request = new UserPostRequest(
          VALID_USER_POST_REQUEST.password(),
          email,
          VALID_USER_POST_REQUEST.firstName(),
          VALID_USER_POST_REQUEST.lastName()
      );
      mockMvc.perform(post("/api/v1/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  void create_returnsBadRequest_whenInvalidFirstName() throws Exception {
    UserPostRequest request = new UserPostRequest(
        VALID_USER_POST_REQUEST.password(),
        VALID_USER_POST_REQUEST.email(),
        "",
        VALID_USER_POST_REQUEST.lastName()
    );

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenInvalidLastName() throws Exception {
    UserPostRequest request = new UserPostRequest(
        VALID_USER_POST_REQUEST.password(),
        VALID_USER_POST_REQUEST.email(),
        VALID_USER_POST_REQUEST.firstName(),
        ""
    );

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + ADMIN_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenAdminUpdatesAnotherUser() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value(VALID_USER_PUT_REQUEST.firstName()))
        .andExpect(jsonPath("$.lastName").value(VALID_USER_PUT_REQUEST.lastName()));

    Optional<User> updatedUser = userRepository.findById(VALID_STAFF.getId());
    assertTrue(updatedUser.isPresent());
    assertEquals(VALID_USER_PUT_REQUEST.firstName(), updatedUser.get().getFirstName());
    assertEquals(VALID_USER_PUT_REQUEST.lastName(), updatedUser.get().getLastName());
  }

  @Test
  void update_returnsForbidden_whenClientUpdatesAnotherUser() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + ADMIN_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenClientUpdatesSelf() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value(VALID_USER_PUT_REQUEST.firstName()))
        .andExpect(jsonPath("$.lastName").value(VALID_USER_PUT_REQUEST.lastName()));

    Optional<User> updatedUser = userRepository.findById(VALID_CLIENT.getId());
    assertTrue(updatedUser.isPresent());
    assertEquals(VALID_USER_PUT_REQUEST.firstName(), updatedUser.get().getFirstName());
    assertEquals(VALID_USER_PUT_REQUEST.lastName(), updatedUser.get().getLastName());
  }

  @Test
  void update_returnsForbidden_whenStaffUpdatesAnotherUser() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_returnsOk_whenStaffUpdatesSelf() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value(VALID_USER_PUT_REQUEST.firstName()))
        .andExpect(jsonPath("$.lastName").value(VALID_USER_PUT_REQUEST.lastName()));

    Optional<User> updatedUser = userRepository.findById(VALID_STAFF.getId());
    assertTrue(updatedUser.isPresent());
    assertEquals(VALID_USER_PUT_REQUEST.firstName(), updatedUser.get().getFirstName());
    assertEquals(VALID_USER_PUT_REQUEST.lastName(), updatedUser.get().getLastName());
  }

  @Test
  void update_returnsNotFound_whenUserNotFound() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + INVALID_USER_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(delete("/api/v1/users/" + VALID_STAFF.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNoContent_whenAuthAsAdmin() throws Exception {
    User staff = userRepository.findById(VALID_STAFF.getId()).orElseThrow();
    boolean checkMailService = staff.getStaffAppointments()
        .stream()
        .anyMatch(appointment ->
            appointment.getStatus() == AppointmentStatus.NOT_APPROVED ||
                appointment.getStatus() == AppointmentStatus.APPROVED
        );

    mockMvc.perform(delete("/api/v1/users/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isNoContent());

    Optional<User> deletedUser = userRepository.findById(VALID_STAFF.getId());
    assertTrue(deletedUser.isEmpty());
    if (checkMailService) {
      verify(mailService, atLeastOnce()).sendCanceledAppointmentNotificationToClient(any());
    }
  }

  @Test
  void delete_returnsForbidden_whenStaffDeletesAnotherUser() throws Exception {
    mockMvc.perform(delete("/api/v1/users/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNoContent_whenStaffDeletesSelf() throws Exception {
    User staff = userRepository.findById(VALID_STAFF.getId()).orElseThrow();
    boolean checkMailService = staff.getStaffAppointments()
        .stream()
        .anyMatch(appointment ->
            appointment.getStatus() == AppointmentStatus.NOT_APPROVED ||
                appointment.getStatus() == AppointmentStatus.APPROVED
        );

    mockMvc.perform(delete("/api/v1/users/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail())))
        .andExpect(status().isNoContent());

    Optional<User> deletedUser = userRepository.findById(VALID_STAFF.getId());
    assertTrue(deletedUser.isEmpty());
    if (checkMailService) {
      verify(mailService, atLeastOnce()).sendCanceledAppointmentNotificationToClient(any());
    }
  }

  @Test
  void delete_returnsForbidden_whenClientDeletesAnotherUser() throws Exception {
    mockMvc.perform(delete("/api/v1/users/" + VALID_STAFF.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_returnsNoContent_whenClientDeletesSelf() throws Exception {
    mockMvc.perform(delete("/api/v1/users/" + VALID_CLIENT.getId())
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail())))
        .andExpect(status().isNoContent());

    Optional<User> deletedUser = userRepository.findById(VALID_CLIENT.getId());
    assertTrue(deletedUser.isEmpty());
  }

  @Test
  void delete_returnsNotFound_whenUserNotFound() throws Exception {
    mockMvc.perform(delete("/api/v1/users/" + INVALID_USER_ID)
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail())))
        .andExpect(status().isNotFound());
  }

  @Test
  void hireStaff_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, VALID_STAFF_DETAILS_DTO))))
        .andExpect(status().isForbidden());
  }

  @Test
  void hireStaff_returnsCreated_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, VALID_STAFF_DETAILS_DTO))))
        .andExpect(status().isCreated());

    Optional<User> staff = userRepository.findByEmail(VALID_USER_POST_REQUEST.email());
    assertTrue(staff.isPresent());
  }

  @Test
  void hireStaff_returnsForbidden_whenAuthAsStaff() throws Exception {
    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, VALID_STAFF_DETAILS_DTO))))
        .andExpect(status().isForbidden());
  }

  @Test
  void hireStaff_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, VALID_STAFF_DETAILS_DTO))))
        .andExpect(status().isForbidden());
  }

  @Test
  void hireStaff_returnsBadRequest_whenUserInfoIsNull() throws Exception {
    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(null, VALID_STAFF_DETAILS_DTO))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenStaffDetailsDtoIsNull() throws Exception {
    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, null))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenInvalidSalary() throws Exception {
    StaffDetailsDto invalidSalaryDto =
        new StaffDetailsDto(-1000.0, LocalTime.of(9, 0), LocalTime.of(17, 0));

    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, invalidSalaryDto))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenInvalidEndWorkingHour() throws Exception {
    StaffDetailsDto invalidEndWorkingHourDto =
        new StaffDetailsDto(1000.0, LocalTime.of(9, 0), null);

    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, invalidEndWorkingHourDto))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenInvalidBeginWorkingHour() throws Exception {
    StaffDetailsDto invalidBeginWorkingHourDto =
        new StaffDetailsDto(1000.0, null, LocalTime.of(17, 0));

    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, invalidBeginWorkingHourDto))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenBeginWorkingHourAfterEndWorkingHour() throws Exception {
    StaffDetailsDto invalidWorkingHoursDto =
        new StaffDetailsDto(1000.0, LocalTime.of(17, 0), LocalTime.of(9, 0));

    mockMvc.perform(post("/api/v1/users/hire")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffHireDto(VALID_USER_POST_REQUEST, invalidWorkingHoursDto))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_STAFF_MODIFY_DTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  void modifyStaff_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_STAFF_MODIFY_DTO)))
        .andExpect(status().isOk());

    Optional<User> staff = userRepository.findById(VALID_STAFF.getId());
    assertTrue(staff.isPresent());
    StaffDetails details = staff.get().getStaffDetails();
    assertNotNull(details);
    assertEquals(VALID_STAFF_MODIFY_DTO.userRole(), staff.get().getRole());
    assertEquals(BigDecimal.valueOf(VALID_STAFF_MODIFY_DTO.salary()), details.getSalary());
    assertEquals(VALID_STAFF_MODIFY_DTO.isAvailable(), details.getIsAvailable());
    assertEquals(VALID_STAFF_MODIFY_DTO.beginWorkingHour(), details.getBeginWorkingHour());
    assertEquals(VALID_STAFF_MODIFY_DTO.endWorkingHour(), details.getEndWorkingHour());
  }

  @Test
  void modifyStaff_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    null,
                    null,
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
                    VALID_STAFF_MODIFY_DTO.endWorkingHour()
                ))))
        .andExpect(status().isOk());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenAuthAsStaffAndTouchAdminFields() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    VALID_STAFF_MODIFY_DTO.userRole(),
                    null,
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
                    VALID_STAFF_MODIFY_DTO.endWorkingHour()
                ))))
        .andExpect(status().isBadRequest());

    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    null,
                    VALID_STAFF_MODIFY_DTO.salary(),
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
                    VALID_STAFF_MODIFY_DTO.endWorkingHour()
                ))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsForbidden_whenAuthAsClient() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    null,
                    null,
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
                    VALID_STAFF_MODIFY_DTO.endWorkingHour())
            )))
        .andExpect(status().isForbidden());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenUserRoleIsClient() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    UserRoles.CLIENT,
                    VALID_STAFF_MODIFY_DTO.salary(),
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
                    VALID_STAFF_MODIFY_DTO.endWorkingHour())
            )))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenSalaryIsInvalid() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    VALID_STAFF_MODIFY_DTO.userRole(),
                    -1000.0,
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
                    VALID_STAFF_MODIFY_DTO.endWorkingHour())
            )))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenIsAvailableIsNull() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    VALID_STAFF_MODIFY_DTO.userRole(),
                    VALID_STAFF_MODIFY_DTO.salary(),
                    null,
                    VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
                    VALID_STAFF_MODIFY_DTO.endWorkingHour()))
            ))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenBeginWorkingHourIsNull() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    VALID_STAFF_MODIFY_DTO.userRole(),
                    VALID_STAFF_MODIFY_DTO.salary(),
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    null,
                    VALID_STAFF_MODIFY_DTO.endWorkingHour()))
            ))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenEndWorkingHourIsNull() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    VALID_STAFF_MODIFY_DTO.userRole(),
                    VALID_STAFF_MODIFY_DTO.salary(),
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
                    null))
            ))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenBeginWorkingHourAfterEndWorkingHour() throws Exception {
    mockMvc.perform(put("/api/v1/users/" + VALID_STAFF.getId() + "/staff")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new StaffModifyDto(
                    VALID_STAFF_MODIFY_DTO.userRole(),
                    VALID_STAFF_MODIFY_DTO.salary(),
                    VALID_STAFF_MODIFY_DTO.isAvailable(),
                    LocalTime.of(17, 0),
                    LocalTime.of(9, 0)))
            ))
        .andExpect(status().isBadRequest());
  }

  @Test
  void resetPassword_returnsForbidden_whenNoAuth() throws Exception {
    mockMvc.perform(put("/api/v1/users/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PASSWORD_UPDATE)))
        .andExpect(status().isForbidden());
  }

  @Test
  void resetPassword_returnsOk_whenAuthAsAdmin() throws Exception {
    mockMvc.perform(put("/api/v1/users/reset-password")
            .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PASSWORD_UPDATE)))
        .andExpect(status().isOk());
  }

  @Test
  void resetPassword_returnsOk_whenAuthAsStaff() throws Exception {
    mockMvc.perform(put("/api/v1/users/reset-password")
            .header("Authorization", generateBarrierAuthHeader(Users.STAFF.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PASSWORD_UPDATE)))
        .andExpect(status().isOk());
  }

  @Test
  void resetPassword_returnsOk_whenAuthAsClient() throws Exception {
    mockMvc.perform(put("/api/v1/users/reset-password")
            .header("Authorization", generateBarrierAuthHeader(Users.CLIENT.getEmail()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PASSWORD_UPDATE)))
        .andExpect(status().isOk());
  }

  @Test
  void resetPassword_returnsBadRequest_whenPasswordIsInvalid() throws Exception {
    List<String> invalidPasswords = List.of(
        "password", "PASSWORD", "Password1", "Password!", "", "Jo", "a".repeat(256)
    );

    for (String password : invalidPasswords) {
      UserPasswordUpdate request = new UserPasswordUpdate(password);

      mockMvc.perform(put("/api/v1/users/reset-password")
              .header("Authorization", generateBarrierAuthHeader(Users.ADMIN.getEmail()))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  private String generateBarrierAuthHeader(String userEmail) {
    String token = jwtService.generateToken(userEmail).jwtToken().token();
    return "Bearer " + token;
  }

  private UserView generateUserView(User user) {
    StaffDetailsView staffDetailsView;
    if (user.getStaffDetails() != null) {
      staffDetailsView = new StaffDetailsView(
          user.getStaffDetails().getSalary(),
          user.getStaffDetails().getProfit(),
          user.getStaffDetails().getCompletedAppointments(),
          user.getStaffDetails().getIsAvailable(),
          user.getStaffDetails().getStartDate(),
          user.getStaffDetails().getBeginWorkingHour(),
          user.getStaffDetails().getEndWorkingHour(),
          user.getServices().stream().map(Service::getId).toList()
      );
    } else {
      staffDetailsView = null;
    }
    return new UserView(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getRole().name(),
        staffDetailsView
    );
  }
}