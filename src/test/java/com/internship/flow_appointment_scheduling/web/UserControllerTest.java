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

  @BeforeEach
  void setUp() {
    authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void getAll_returnsOk_whenUserRoleIsProvided() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    UserRoles userRole = UserRoles.EMPLOYEE;
    UserView userView = mock(UserView.class);
    Page<UserView> userViews = new PageImpl<>(Collections.singletonList(userView), pageable, 1);

    when(userService.getAll(pageable, userRole)).thenReturn(userViews);

    mockMvc.perform(get("/api/v1/users")
            .param("page", "0")
            .param("size", "10")
            .param("userRole", userRole.name()))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getAll_returnsOk_whenUserRoleIsNotProvided() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    UserView userView = mock(UserView.class);
    Page<UserView> userViews = new PageImpl<>(Collections.singletonList(userView), pageable, 1);

    when(userService.getAll(pageable, null)).thenReturn(userViews);

    mockMvc.perform(get("/api/v1/users")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getAllByServiceId_returnsOk_whenValidParams() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    Long serviceId = 1L;
    UserView userView = mock(UserView.class);
    Page<UserView> userViews = new PageImpl<>(Collections.singletonList(userView), pageable, 1);

    when(userService.getAllByServiceId(pageable, serviceId)).thenReturn(userViews);

    mockMvc.perform(get("/api/v1/users/service/{serviceId}", serviceId)
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getById_returnsOk_whenUserExists() throws Exception {
    Long userId = 1L;
    UserView userView = mock(UserView.class);

    when(userService.getById(userId)).thenReturn(userView);

    mockMvc.perform(get("/api/v1/users/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void getById_returnsNotFound_whenUserDoesNotExist() throws Exception {
    Long userId = 1L;

    when(userService.getById(userId))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND));

    mockMvc.perform(get("/api/v1/users/{id}", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_returnsCreated_whenRequestIsValid() throws Exception {
    UserView userView = mock(UserView.class);
    UserPostRequest validUserPostRequest = new UserPostRequest(
        "ValidPass123!",
        "valid.email@example.com",
        "John",
        "Doe"
    );
    when(userService.create(validUserPostRequest)).thenReturn(userView);
    when(userRepository.existsByEmail(validUserPostRequest.email())).thenReturn(false);

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validUserPostRequest)))
        .andExpect(status().isCreated());
  }

  @Test
  void create_returnsBadRequest_whenPasswordIsInvalid() throws Exception {
    UserPostRequest blankPassword = new UserPostRequest(
        "",
        "valid.email@example.com",
        "John",
        "Doe"
    );

    UserPostRequest shortPassword = new UserPostRequest(
        "Short1!",
        "valid.email@example.com",
        "John",
        "Doe"
    );

    UserPostRequest invalidPassword = new UserPostRequest(
        "invalidpassword",
        "valid.email@example.com",
        "John",
        "Doe"
    );

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blankPassword)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(shortPassword)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidPassword)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenEmailIsInvalid() throws Exception {
    UserPostRequest blankEmail = new UserPostRequest(
        "ValidPass123!",
        "",
        "John",
        "Doe"
    );
    UserPostRequest invalidEmail = new UserPostRequest(
        "ValidPass123!",
        "invalid-email",
        "John",
        "Doe"
    );
    UserPostRequest alreadyTaken = new UserPostRequest(
        "ValidPass123!",
        "alreadyTaken@abv.bg",
        "John",
        "Doe"
    );

    when(userRepository.existsByEmail(alreadyTaken.email())).thenReturn(true);

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blankEmail)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidEmail)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(alreadyTaken)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenFirstNameIsTooShort() throws Exception {
    UserPostRequest request = new UserPostRequest(
        "ValidPass123!",
        "valid.email@example.com",
        "Jo",
        "Doe"
    );

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenLastNameIsTooShort() throws Exception {
    UserPostRequest request = new UserPostRequest(
        "ValidPass123!",
        "valid.email@example.com",
        "John",
        "Do"
    );

    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsOk_whenRequestIsValid() throws Exception {
    Long userId = 1L;
    UserPutRequest validUserPutRequest = new UserPutRequest("John", "Doe");
    UserView userView = mock(UserView.class);

    when(userService.update(userId, validUserPutRequest)).thenReturn(userView);

    mockMvc.perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validUserPutRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void update_returnsNotFound_whenUserDoesNotExist() throws Exception {
    UserPutRequest validUserPutRequest = new UserPutRequest("John", "Doe");
    Long userId = 1L;
    when(userService.update(userId, validUserPutRequest))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND, userId));

    mockMvc.perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validUserPutRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_returnsBadRequest_whenFirstNameIsInvalid() throws Exception {
    Long userId = 1L;
    UserPutRequest blankFirstName = new UserPutRequest("", "Doe");
    UserPutRequest nullFirstName = new UserPutRequest(null, "Doe");
    UserPutRequest toShortFirstName = new UserPutRequest("Jo", "Doe");

    mockMvc.perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blankFirstName)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nullFirstName)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(toShortFirstName)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsBadRequest_whenLastNameIsInvalid() throws Exception {
    Long userId = 1L;
    UserPutRequest blankLastName = new UserPutRequest("John", "");
    UserPutRequest nullLastName = new UserPutRequest("John", null);
    UserPutRequest toShortLastName = new UserPutRequest("John", "Do");

    mockMvc.perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blankLastName)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nullLastName)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(toShortLastName)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void delete_returnsNoContent_whenUserExists() throws Exception {
    Long userId = 1L;

    mockMvc.perform(delete("/api/v1/users/{id}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  void delete_returnsNotFound_whenUserDoesNotExist() throws Exception {
    Long userId = 1L;

    doThrow(new NotFoundException(Exceptions.USER_NOT_FOUND, userId))
        .when(userService).delete(userId);

    mockMvc.perform(delete("/api/v1/users/{id}", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  void hireStaff_returnsCreated_whenRequestIsValid() throws Exception {
    StaffHireDto validStaffHireDto = getValidStaffHireDto();
    UserView userView = mock(UserView.class);

    when(userService.hireStaff(validStaffHireDto)).thenReturn(userView);
    when(userRepository.existsByEmail(validStaffHireDto.userInfo().email())).thenReturn(false);

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validStaffHireDto)))
        .andExpect(status().isCreated());
  }

  @Test
  void hireStaff_returnsBadRequest_whenUserInfoIsNull() throws Exception {
    StaffHireDto request = new StaffHireDto(null, getValidStaffHireDto().staffDetailsDto());

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenStaffDetailsDtoIsNull() throws Exception {
    StaffHireDto request = new StaffHireDto(getValidStaffHireDto().userInfo(), null);

    mockMvc.perform(post("/api/v1/users/hire")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hireStaff_returnsBadRequest_whenStaffDetailsInvalidSalary() throws Exception {
    StaffHireDto validStaffHireDto = getValidStaffHireDto();
    StaffDetailsDto invalidSalary = new StaffDetailsDto(-1.0, LocalTime.now(),
        LocalTime.now().plusMinutes(30));
    StaffDetailsDto nullSalary = new StaffDetailsDto(null, LocalTime.now(),
        LocalTime.now().plusMinutes(30));
    StaffHireDto request1 = new StaffHireDto(validStaffHireDto.userInfo(), invalidSalary);
    StaffHireDto request2 = new StaffHireDto(validStaffHireDto.userInfo(), nullSalary);

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
    StaffHireDto validStaffHireDto = getValidStaffHireDto();
    StaffDetailsDto invalidBegin = new StaffDetailsDto(25.0, null, LocalTime.now().plusMinutes(30));
    StaffDetailsDto invalidEnd = new StaffDetailsDto(null, LocalTime.now(), null);
    StaffDetailsDto invalidWorkingHours = new StaffDetailsDto(null, LocalTime.now(),
        LocalTime.now());
    StaffHireDto request1 = new StaffHireDto(validStaffHireDto.userInfo(), invalidBegin);
    StaffHireDto request2 = new StaffHireDto(validStaffHireDto.userInfo(), invalidEnd);
    StaffHireDto request3 = new StaffHireDto(validStaffHireDto.userInfo(), invalidWorkingHours);

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
    Long userId = 1L;
    StaffModifyDto validStaffModifyDto = new StaffModifyDto(
        null,
        null,
        true,
        LocalTime.of(9, 0),
        LocalTime.of(17, 0)
    );
    UserView userView = mock(UserView.class);

    when(authentication.isAuthenticated()).thenReturn(true);
    when(userService.modifyStaff(userId, validStaffModifyDto)).thenReturn(userView);

    mockMvc.perform(put("/api/v1/users/{id}/staff", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validStaffModifyDto)))
        .andExpect(status().isOk());
  }

  @Test
  void modifyStaff_returnsNotFound_whenUserDoesNotExist() throws Exception {
    Long userId = 1L;
    StaffModifyDto validStaffModifyDto = new StaffModifyDto(
        null,
        null,
        true,
        LocalTime.of(9, 0),
        LocalTime.of(17, 0)
    );

    when(authentication.isAuthenticated()).thenReturn(true);
    when(userService.modifyStaff(userId, validStaffModifyDto))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND, userId));

    mockMvc.perform(put("/api/v1/users/{id}/staff", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validStaffModifyDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenIsAvailableIsNull() throws Exception {
    Long userId = 1L;
    StaffModifyDto request = new StaffModifyDto(
        UserRoles.EMPLOYEE,
        1000.0,
        null,
        LocalTime.of(9, 0),
        LocalTime.of(17, 0)
    );

    mockMvc.perform(put("/api/v1/users/{id}/staff", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenBeginWorkingHourIsNull() throws Exception {
    Long userId = 1L;
    StaffModifyDto request = new StaffModifyDto(
        UserRoles.EMPLOYEE,
        1000.0,
        true,
        null,
        LocalTime.of(17, 0)
    );

    mockMvc.perform(put("/api/v1/users/{id}/staff", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void modifyStaff_returnsBadRequest_whenEndWorkingHourIsNull() throws Exception {
    Long userId = 1L;
    StaffModifyDto request = new StaffModifyDto(
        UserRoles.EMPLOYEE,
        1000.0,
        true,
        LocalTime.of(9, 0),
        null
    );

    mockMvc.perform(put("/api/v1/users/{id}/staff", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  private StaffHireDto getValidStaffHireDto() {
    UserPostRequest userPostRequest = new UserPostRequest(
        "ValidPass123!",
        "valid.email@example.com",
        "John",
        "Doe"
    );
    StaffDetailsDto staffDetailsDto =
        new StaffDetailsDto(25.0,
            LocalTime.of(9, 0),
            LocalTime.of(17, 0));
    return new StaffHireDto(userPostRequest, staffDetailsDto);
  }
}