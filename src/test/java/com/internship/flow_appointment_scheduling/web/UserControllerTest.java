package com.internship.flow_appointment_scheduling.web;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffDetailsDto;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffHireDto;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffModifyDto;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtService;
import java.time.LocalTime;
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

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;

  private Authentication authentication;

  @Autowired
  private ObjectMapper objectMapper;
  private static final Pageable PAGEABLE = PageRequest.of(0, 10);
  private static final Long VALID_USER_ID = 1L;
  private static final UserPostRequest VALID_USER_POST_REQUEST = new UserPostRequest(
      "ValidPass123!",
      "valid.email@example.com",
      "John",
      "Doe"
  );
  private static final UserPutRequest VALID_USER_PUT_REQUEST = new UserPutRequest("John", "Doe");
  private static final StaffDetailsDto VALID_STAFF_DETAILS_DTO = new StaffDetailsDto(
      25.0,
      LocalTime.now(),
      LocalTime.now().plusMinutes(30)
  );
  private static final StaffHireDto VALID_STAFF_HIRE_DTO = new StaffHireDto(
      VALID_USER_POST_REQUEST,
      VALID_STAFF_DETAILS_DTO
  );
  private static final StaffModifyDto VALID_STAFF_MODIFY_DTO = new StaffModifyDto(
      UserRoles.EMPLOYEE,
      1000.0,
      true,
      LocalTime.of(9, 0),
      LocalTime.of(17, 0)
  );

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void getAll_returnsOk_whenUserRoleIsProvided() throws Exception {
    UserRoles userRole = UserRoles.EMPLOYEE;
    UserView userView = mock(UserView.class);
    Page<UserView> userViews = new PageImpl<>(Collections.singletonList(userView), PAGEABLE, 1);

    when(userService.getAll(PAGEABLE, userRole)).thenReturn(userViews);

    mockMvc.perform(get("/api/v1/users")
            .param("page", "0")
            .param("size", "10")
            .param("userRole", userRole.name()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getAll_returnsOk_whenUserRoleIsNotProvided() throws Exception {
    UserView userView = mock(UserView.class);
    Page<UserView> userViews = new PageImpl<>(Collections.singletonList(userView), PAGEABLE, 1);

    when(userService.getAll(PAGEABLE, null)).thenReturn(userViews);

    mockMvc.perform(get("/api/v1/users")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getAllByServiceId_returnsOk_whenValidParams() throws Exception {
    Long serviceId = 1L;
    UserView userView = mock(UserView.class);
    Page<UserView> userViews = new PageImpl<>(Collections.singletonList(userView), PAGEABLE, 1);

    when(userService.getAllByServiceId(PAGEABLE, serviceId)).thenReturn(userViews);

    mockMvc.perform(get("/api/v1/users/service/{serviceId}", serviceId)
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getById_returnsOk_whenUserExists() throws Exception {
    UserView userView = mock(UserView.class);

    when(userService.getById(VALID_USER_ID)).thenReturn(userView);

    mockMvc.perform(get("/api/v1/users/{id}", VALID_USER_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getById_returnsNotFound_whenUserDoesNotExist() throws Exception {
    Long notExistingUser = 1L;

    when(userService.getById(notExistingUser))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND));

    mockMvc.perform(get("/api/v1/users/{id}", notExistingUser))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_returnsCreated_whenRequestIsValid() throws Exception {
    UserView userView = mock(UserView.class);

    when(userService.create(VALID_USER_POST_REQUEST)).thenReturn(userView);
    when(userRepository.existsByEmail(VALID_USER_POST_REQUEST.email())).thenReturn(false);

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_POST_REQUEST)))
        .andExpect(status().isCreated());
  }

  @Test
  void create_returnsBadRequest_whenPasswordIsInvalid() throws Exception {
    List<String> invalidPasswords = List.of("", "short1!", "onlyloowercase");

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
  void create_returnsBadRequest_whenEmailIsInvalid() throws Exception {
    List<String> invalidEmails = List.of("", "invalid-email", "invalid@.com", "invalid.com");

    for (String email : invalidEmails) {
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

    UserPostRequest alreadyTaken = new UserPostRequest(
        VALID_USER_POST_REQUEST.password(),
        "alreadyTaken@abv.bg",
        VALID_USER_POST_REQUEST.firstName(),
        VALID_USER_POST_REQUEST.lastName()
    );

    when(userRepository.existsByEmail(alreadyTaken.email())).thenReturn(true);

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(alreadyTaken)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenFirstNameIsTooShort() throws Exception {
    UserPostRequest request = new UserPostRequest(
        VALID_USER_POST_REQUEST.password(),
        VALID_USER_POST_REQUEST.email(),
        "Jo",
        VALID_USER_POST_REQUEST.lastName()
    );

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenLastNameIsTooShort() throws Exception {
    UserPostRequest request = new UserPostRequest(
        VALID_USER_POST_REQUEST.password(),
        VALID_USER_POST_REQUEST.email(),
        VALID_USER_POST_REQUEST.firstName(),
        "Do"
    );

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsOk_whenRequestIsValid() throws Exception {
    UserView userView = mock(UserView.class);

    when(userService.update(VALID_USER_ID, VALID_USER_PUT_REQUEST)).thenReturn(userView);

    mockMvc.perform(put("/api/v1/users/{id}", VALID_USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isOk());
  }

  @Test
  void update_returnsNotFound_whenUserDoesNotExist() throws Exception {
    Long invalidUserId = 1L;

    when(userService.update(invalidUserId, VALID_USER_PUT_REQUEST))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND, invalidUserId));

    mockMvc.perform(put("/api/v1/users/{id}", invalidUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_USER_PUT_REQUEST)))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_returnsBadRequest_whenFirstNameIsInvalid() throws Exception {
    List<String> invalidNames = List.of("", "Jo");

    for (String name : invalidNames) {
      UserPutRequest request = new UserPutRequest(name, VALID_USER_PUT_REQUEST.lastName());

      mockMvc.perform(put("/api/v1/users/{id}", VALID_USER_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    UserPutRequest nullName = new UserPutRequest(null, VALID_USER_PUT_REQUEST.lastName());

    mockMvc.perform(put("/api/v1/users/{id}", VALID_USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nullName)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenLastNameIsInvalid() throws Exception {
    List<String> invalidNames = List.of("", "Do");

    for (String name : invalidNames) {
      UserPutRequest request = new UserPutRequest(VALID_USER_PUT_REQUEST.firstName(), name);

      mockMvc.perform(put("/api/v1/users/{id}", VALID_USER_ID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    UserPutRequest nullRequest = new UserPutRequest(VALID_USER_PUT_REQUEST.firstName(), null);

    mockMvc.perform(put("/api/v1/users/{id}", VALID_USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nullRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void delete_returnsNoContent_whenUserExists() throws Exception {
    mockMvc.perform(delete("/api/v1/users/{id}", VALID_USER_ID))
        .andExpect(status().isNoContent());
  }

  @Test
  void delete_returnsNotFound_whenUserDoesNotExist() throws Exception {
    Long invalidUserId = 1L;

    doThrow(new NotFoundException(Exceptions.USER_NOT_FOUND, invalidUserId))
        .when(userService).delete(invalidUserId);

    mockMvc.perform(delete("/api/v1/users/{id}", invalidUserId))
        .andExpect(status().isNotFound());
  }

  @Test
  void hireStaff_returnsCreated_whenRequestIsValid() throws Exception {
    UserView userView = mock(UserView.class);

    when(userService.hireStaff(VALID_STAFF_HIRE_DTO)).thenReturn(userView);
    when(userRepository.existsByEmail(VALID_STAFF_HIRE_DTO.userInfo().email())).thenReturn(false);

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_STAFF_HIRE_DTO)))
        .andExpect(status().isCreated());
  }

  @Test
  void hireStaff_returnsBadRequest_whenUserInfoIsNull() throws Exception {
    StaffHireDto request = new StaffHireDto(null, VALID_STAFF_DETAILS_DTO);

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenStaffDetailsDtoIsNull() throws Exception {
    StaffHireDto request = new StaffHireDto(VALID_USER_POST_REQUEST, null);

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenStaffDetailsInvalidSalary() throws Exception {
    StaffDetailsDto invalidSalary = new StaffDetailsDto(
        -1.0,
        VALID_STAFF_DETAILS_DTO.beginWorkingHour(),
        VALID_STAFF_DETAILS_DTO.endWorkingHour()
    );
    StaffDetailsDto nullSalary = new StaffDetailsDto(
        null,
        VALID_STAFF_DETAILS_DTO.beginWorkingHour(),
        VALID_STAFF_DETAILS_DTO.endWorkingHour()
    );
    StaffHireDto request1 = new StaffHireDto(VALID_USER_POST_REQUEST, invalidSalary);
    StaffHireDto request2 = new StaffHireDto(VALID_USER_POST_REQUEST, nullSalary);

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request1)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request2)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenStaffDetailsInvalidWorkingHours() throws Exception {
    StaffDetailsDto invalidBegin = new StaffDetailsDto(
        VALID_STAFF_DETAILS_DTO.salary(),
        null,
        VALID_STAFF_DETAILS_DTO.endWorkingHour()
    );
    StaffDetailsDto invalidEnd = new StaffDetailsDto(
        VALID_STAFF_DETAILS_DTO.salary(),
        VALID_STAFF_DETAILS_DTO.beginWorkingHour(),
        null
    );
    StaffDetailsDto invalidWorkingHours = new StaffDetailsDto(
        VALID_STAFF_DETAILS_DTO.salary(),
        LocalTime.now(),
        LocalTime.now()
    );
    StaffHireDto request1 = new StaffHireDto(VALID_USER_POST_REQUEST, invalidBegin);
    StaffHireDto request2 = new StaffHireDto(VALID_USER_POST_REQUEST, invalidEnd);
    StaffHireDto request3 = new StaffHireDto(VALID_USER_POST_REQUEST, invalidWorkingHours);

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request1)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request2)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request3)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsOk_whenRequestIsValid() throws Exception {
    StaffModifyDto validStaffModifyDto = new StaffModifyDto(
        null,
        null,
        true,
        LocalTime.of(9, 0),
        LocalTime.of(17, 0)
    );
    UserView userView = mock(UserView.class);

    when(authentication.isAuthenticated()).thenReturn(true);
    when(userService.modifyStaff(VALID_USER_ID, validStaffModifyDto)).thenReturn(userView);

    mockMvc.perform(put("/api/v1/users/{id}/staff", VALID_USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validStaffModifyDto)))
        .andExpect(status().isOk());
  }

  @Test
  void modifyStaff_returnsNotFound_whenUserDoesNotExist() throws Exception {
    Long invalidUserId = -1L;
    StaffModifyDto validStaffModifyDto = new StaffModifyDto(
        null,
        null,
        VALID_STAFF_MODIFY_DTO.isAvailable(),
        VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
        VALID_STAFF_MODIFY_DTO.endWorkingHour()
    );

    when(authentication.isAuthenticated()).thenReturn(true);
    when(userService.modifyStaff(invalidUserId, validStaffModifyDto))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND, invalidUserId));

    mockMvc.perform(put("/api/v1/users/{id}/staff", invalidUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validStaffModifyDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenIsAvailableIsNull() throws Exception {
    StaffModifyDto request = new StaffModifyDto(
        VALID_STAFF_MODIFY_DTO.userRole(),
        VALID_STAFF_MODIFY_DTO.salary(),
        null,
        VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
        VALID_STAFF_MODIFY_DTO.endWorkingHour()
    );

    mockMvc.perform(put("/api/v1/users/{id}/staff", VALID_USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenBeginWorkingHourIsNull() throws Exception {
    StaffModifyDto request = new StaffModifyDto(
        VALID_STAFF_MODIFY_DTO.userRole(),
        VALID_STAFF_MODIFY_DTO.salary(),
        VALID_STAFF_MODIFY_DTO.isAvailable(),
        null,
        VALID_STAFF_MODIFY_DTO.endWorkingHour()
    );

    mockMvc.perform(put("/api/v1/users/{id}/staff", VALID_USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenEndWorkingHourIsNull() throws Exception {
    StaffModifyDto request = new StaffModifyDto(
        VALID_STAFF_MODIFY_DTO.userRole(),
        VALID_STAFF_MODIFY_DTO.salary(),
        VALID_STAFF_MODIFY_DTO.isAvailable(),
        VALID_STAFF_MODIFY_DTO.beginWorkingHour(),
        null
    );

    mockMvc.perform(put("/api/v1/users/{id}/staff", VALID_USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}