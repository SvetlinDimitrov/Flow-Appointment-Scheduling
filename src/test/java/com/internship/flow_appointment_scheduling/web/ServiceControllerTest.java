package com.internship.flow_appointment_scheduling.web;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.service.ServiceService;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import com.internship.flow_appointment_scheduling.features.work_space.repository.WorkSpaceRepository;
import com.internship.flow_appointment_scheduling.features.work_space.service.WorkSpaceService;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.security.service.JwtService;
import java.math.BigDecimal;
import java.time.Duration;
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

@WebMvcTest(controllers = ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ServiceService serviceService;
  @MockBean
  private WorkSpaceService workSpaceService;
  @MockBean
  private WorkSpaceRepository workSpaceRepository;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void getAll_returnsEmptyPage_whenNoServicesExist() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    Page<ServiceView> emptyPage = new PageImpl<>(Collections.emptyList());

    when(serviceService.getAll(pageable, null)).thenReturn(emptyPage);

    mockMvc.perform(get("/api/v1/services")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isEmpty());
  }

  @Test
  void getAll_returnsPageWithServices_whenServicesExist() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    ServiceView serviceView = mock(ServiceView.class);
    Page<ServiceView> servicePage = new PageImpl<>(Collections.singletonList(serviceView));

    when(serviceService.getAll(pageable, null)).thenReturn(servicePage);

    mockMvc.perform(get("/api/v1/services")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isNotEmpty());
  }

  @Test
  void getAll_returnsPageWithServices_whenFilteredByStaffEmail() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    String staffEmail = "staff@example.com";
    ServiceView serviceView = mock(ServiceView.class);
    Page<ServiceView> servicePage = new PageImpl<>(Collections.singletonList(serviceView));

    when(serviceService.getAll(pageable, staffEmail)).thenReturn(servicePage);

    mockMvc.perform(get("/api/v1/services")
            .param("page", "0")
            .param("size", "10")
            .param("staffEmail", staffEmail))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isNotEmpty());
  }

  @Test
  void getById_returnsOk_whenServiceExists() throws Exception {
    Long id = 1L;
    ServiceView serviceView = mock(ServiceView.class);

    when(serviceView.id()).thenReturn(id);
    when(serviceService.getById(id)).thenReturn(serviceView);

    mockMvc.perform(get("/api/v1/services/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id));
  }

  @Test
  void getById_returnsNotFound_whenServiceDoesNotExist() throws Exception {
    Long nonExistentId = 999L;

    when(serviceService.getById(nonExistentId)).thenThrow(
        new NotFoundException(Exceptions.SERVICE_NOT_FOUND)
    );

    mockMvc.perform(get("/api/v1/services/{id}", nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAllWorkSpacesNames_returnsOk_withListOfWorkSpaceNames() throws Exception {
    List<String> workSpaceNames = List.of("Workspace1", "Workspace2");

    when(workSpaceService.getAllNames()).thenReturn(workSpaceNames);

    mockMvc.perform(get("/api/v1/services/workspaces"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0]").value("Workspace1"))
        .andExpect(jsonPath("$[1]").value("Workspace2"));
  }

  @Test
  void create_returnsOk_whenAllFieldsAreValid() throws Exception {
    ServiceDTO serviceDTO = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    ServiceView serviceView = mock(ServiceView.class);
    when(serviceService.create(serviceDTO)).thenReturn(serviceView);
    when(workSpaceRepository.existsByName(serviceDTO.workSpaceName())).thenReturn(true);

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void create_returnsBadRequest_whenNameIsInvalid() throws Exception {

    List<String> invalidNames = List.of("", "Na");

    for (String n : invalidNames) {
      ServiceDTO serviceDTO = new ServiceDTO(
          n,
          "Valid Description",
          true,
          new BigDecimal("10.0"),
          Duration.ofHours(1),
          "ValidWorkSpace"
      );

      mockMvc.perform(post("/api/v1/services")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(serviceDTO)))
          .andExpect(status().isBadRequest());
    }

    ServiceDTO serviceDtoWithNull = new ServiceDTO(
        null,
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithNull)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDescriptionIsInvalid() throws Exception {
    ServiceDTO serviceDTO = new ServiceDTO(
        "Valid Name",
        "",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isBadRequest());

    ServiceDTO serviceDtoWitchNull = new ServiceDTO(
        "Valid Name",
        null,
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWitchNull)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenAvailabilityIsNull() throws Exception {
    ServiceDTO serviceDTO = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        null,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenPriceIsInvalid() throws Exception {
    ServiceDTO serviceDTO = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        new BigDecimal("0.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isBadRequest());

    ServiceDTO serviceDtoWithNullPrice = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        null,
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithNullPrice)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDurationIsInvalid() throws Exception {
    ServiceDTO serviceDTO = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(-1),
        "ValidWorkSpace"
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isBadRequest());

    ServiceDTO serviceDtoWithNullDuration = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        null,
        "ValidWorkSpace"
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithNullDuration)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenWorkSpaceNameIsInvalid() throws Exception {
    ServiceDTO serviceDTO = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        ""
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isBadRequest());

    String invalidWorkSpaceName = "Invalid WorkSpace Name";
    ServiceDTO serviceDtoWithInvalidName = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        invalidWorkSpaceName
    );

    when(workSpaceRepository.existsByName(invalidWorkSpaceName)).thenReturn(false);

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsOk_whenAssignmentIsSuccessful() throws Exception {
    Long id = 1L;
    String staffEmail = "staff@example.com";
    ServiceView serviceView = mock(ServiceView.class);
    User user = mock(User.class);

    when(userService.findByEmail(staffEmail)).thenReturn(user);
    when(user.getRole()).thenReturn(UserRoles.EMPLOYEE);
    when(serviceService.assignStaff(id, staffEmail)).thenReturn(serviceView);

    mockMvc.perform(post("/api/v1/services/{id}/assign", id)
            .param("staffEmail", staffEmail))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void assignStaff_returnsBadRequest_whenEmailIsNotStaffOrAdministrator() throws Exception {
    Long id = 1L;
    String staffEmail = "staff@example.com";
    User user = mock(User.class);

    when(userService.findByEmail(staffEmail)).thenReturn(user);
    when(user.getRole()).thenReturn(UserRoles.CLIENT);

    mockMvc.perform(post("/api/v1/services/{id}/assign", id)
            .param("staffEmail", staffEmail))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsNotFound_whenServiceNotFound() throws Exception {
    Long id = 999L;
    String staffEmail = "staff@example.com";
    User user = mock(User.class);

    when(userService.findByEmail(staffEmail)).thenReturn(user);
    when(user.getRole()).thenReturn(UserRoles.EMPLOYEE);
    when(serviceService.assignStaff(id, staffEmail)).thenThrow(
        new NotFoundException(Exceptions.SERVICE_NOT_FOUND));

    mockMvc.perform(post("/api/v1/services/{id}/assign", id)
            .param("staffEmail", staffEmail))
        .andExpect(status().isNotFound());
  }

  @Test
  void assignStaff_returnsNotFound_whenStaffEmailNotFound() throws Exception {
    Long id = 1L;
    String staffEmail = "nonexistent@example.com";
    User user = mock(User.class);

    when(userService.findByEmail(staffEmail)).thenReturn(user);
    when(user.getRole()).thenReturn(UserRoles.EMPLOYEE);
    when(serviceService.assignStaff(id, staffEmail)).thenThrow(
        new NotFoundException(Exceptions.USER_NOT_FOUND));

    mockMvc.perform(post("/api/v1/services/{id}/assign", id)
            .param("staffEmail", staffEmail))
        .andExpect(status().isNotFound());
  }

  @Test
  void unassignStaff_returnsOk_whenUnassignmentIsSuccessful() throws Exception {
    Long id = 1L;
    String staffEmail = "staff@example.com";
    ServiceView serviceView = mock(ServiceView.class);

    when(serviceService.unassignStaff(id, staffEmail)).thenReturn(serviceView);

    mockMvc.perform(put("/api/v1/services/{id}/unassign", id)
            .param("staffEmail", staffEmail))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void unassignStaff_returnsNotFound_whenServiceNotFound() throws Exception {
    Long id = 999L;
    String staffEmail = "staff@example.com";

    when(serviceService.unassignStaff(id, staffEmail))
        .thenThrow(new NotFoundException(Exceptions.SERVICE_NOT_FOUND));

    mockMvc.perform(put("/api/v1/services/{id}/unassign", id)
            .param("staffEmail", staffEmail))
        .andExpect(status().isNotFound());
  }

  @Test
  void unassignStaff_returnsNotFound_whenStaffEmailNotFound() throws Exception {
    Long id = 1L;
    String staffEmail = "nonexistent@example.com";

    when(serviceService.unassignStaff(id, staffEmail))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND));

    mockMvc.perform(put("/api/v1/services/{id}/unassign", id)
            .param("staffEmail", staffEmail))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_returnsOk_whenUpdateIsSuccessful() throws Exception {
    Long id = 1L;
    ServiceDTO serviceDTO = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );
    ServiceView serviceView = mock(ServiceView.class);

    when(workSpaceRepository.existsByName(serviceDTO.workSpaceName())).thenReturn(true);
    when(serviceService.update(id, serviceDTO)).thenReturn(serviceView);

    mockMvc.perform(put("/api/v1/services/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void update_returnsBadRequest_whenRequestIsInvalid() throws Exception {
    Long id = 1L;
    ServiceDTO invalidServiceDTO = new ServiceDTO(
        "",
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    mockMvc.perform(put("/api/v1/services/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidServiceDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsNotFound_whenServiceNotFound() throws Exception {
    Long id = 999L;
    ServiceDTO serviceDTO = new ServiceDTO(
        "Valid Name",
        "Valid Description",
        true,
        new BigDecimal("10.0"),
        Duration.ofHours(1),
        "ValidWorkSpace"
    );

    when(workSpaceRepository.existsByName(serviceDTO.workSpaceName())).thenReturn(true);
    when(serviceService.update(id, serviceDTO)).thenThrow(
        new NotFoundException(Exceptions.SERVICE_NOT_FOUND));

    mockMvc.perform(put("/api/v1/services/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTO)))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_returnsNoContent_whenDeletionIsSuccessful() throws Exception {
    Long id = 1L;

    mockMvc.perform(delete("/api/v1/services/{id}", id))
        .andExpect(status().isNoContent());
  }

  @Test
  void delete_returnsNotFound_whenServiceNotFound() throws Exception {
    Long id = 999L;

    doThrow(new NotFoundException(Exceptions.SERVICE_NOT_FOUND))
        .when(serviceService).delete(id);

    mockMvc.perform(delete("/api/v1/services/{id}", id))
        .andExpect(status().isNotFound());
  }
}