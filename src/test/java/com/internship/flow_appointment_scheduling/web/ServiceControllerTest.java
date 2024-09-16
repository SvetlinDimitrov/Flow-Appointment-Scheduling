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

  private static final Pageable PAGEABLE = PageRequest.of(0, 10);
  private static final Long VALID_SERVICE_ID = 1L;
  private static final Long INVALID_SERVICE_ID = -1L;
  private static final String VALID_STAFF_EMAIL = "staff1@flow.com";
  private static final ServiceDTO VALID_SERVICE_DTO = new ServiceDTO(
      "Valid Name",
      "Valid Description",
      true,
      new BigDecimal("10.0"),
      Duration.ofHours(1),
      "ValidWorkSpace"
  );

  @BeforeEach
  void setUp() {
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void getAll_returnsEmptyPage_whenNoServicesExist() throws Exception {
    Page<ServiceView> emptyPage = new PageImpl<>(Collections.emptyList());

    when(serviceService.getAll(PAGEABLE, null)).thenReturn(emptyPage);

    mockMvc.perform(get("/api/v1/services")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isEmpty());
  }

  @Test
  void getAll_returnsPageWithServices_whenServicesExist() throws Exception {
    ServiceView serviceView = mock(ServiceView.class);
    Page<ServiceView> servicePage = new PageImpl<>(Collections.singletonList(serviceView));

    when(serviceService.getAll(PAGEABLE, null)).thenReturn(servicePage);

    mockMvc.perform(get("/api/v1/services")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isNotEmpty());
  }

  @Test
  void getAll_returnsPageWithServices_whenFilteredByStaffEmail() throws Exception {
    ServiceView serviceView = mock(ServiceView.class);
    Page<ServiceView> servicePage = new PageImpl<>(Collections.singletonList(serviceView));

    when(serviceService.getAll(PAGEABLE, VALID_STAFF_EMAIL)).thenReturn(servicePage);

    mockMvc.perform(get("/api/v1/services")
            .param("page", "0")
            .param("size", "10")
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isNotEmpty());
  }

  @Test
  void getById_returnsOk_whenServiceExists() throws Exception {
    ServiceView serviceView = mock(ServiceView.class);

    when(serviceView.id()).thenReturn(VALID_SERVICE_ID);
    when(serviceService.getById(VALID_SERVICE_ID)).thenReturn(serviceView);

    mockMvc.perform(get("/api/v1/services/{id}", VALID_SERVICE_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(VALID_SERVICE_ID));
  }

  @Test
  void getById_returnsNotFound_whenServiceDoesNotExist() throws Exception {
    when(serviceService.getById(INVALID_SERVICE_ID)).thenThrow(
        new NotFoundException(Exceptions.SERVICE_NOT_FOUND)
    );

    mockMvc.perform(get("/api/v1/services/{id}", INVALID_SERVICE_ID))
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
    ServiceView serviceView = mock(ServiceView.class);
    when(serviceService.create(VALID_SERVICE_DTO)).thenReturn(serviceView);
    when(workSpaceRepository.existsByName(VALID_SERVICE_DTO.workSpaceName())).thenReturn(true);

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void create_returnsBadRequest_whenNameIsInvalid() throws Exception {
    List<String> invalidNames = List.of("", "Na");

    for (String n : invalidNames) {
      ServiceDTO invalidDto = new ServiceDTO(
          n,
          VALID_SERVICE_DTO.description(),
          VALID_SERVICE_DTO.availability(),
          VALID_SERVICE_DTO.price(),
          VALID_SERVICE_DTO.duration(),
          VALID_SERVICE_DTO.workSpaceName()
      );

      mockMvc.perform(post("/api/v1/services")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDto)))
          .andExpect(status().isBadRequest());
    }

    ServiceDTO serviceDtoWithNull = new ServiceDTO(
        null,
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithNull)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDescriptionIsInvalid() throws Exception {
    ServiceDTO serviceDtoEmptyDescription = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        "",
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );
    ServiceDTO serviceDtoWitchNull = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        null,
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoEmptyDescription)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWitchNull)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenAvailabilityIsNull() throws Exception {
    ServiceDTO serviceDtoWithNullAvailability = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        null,
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithNullAvailability)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenPriceIsInvalid() throws Exception {
    ServiceDTO serviceDtoWithZeroPrice = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        new BigDecimal("0.0"),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );
    ServiceDTO serviceDtoWithNullPrice = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        null,
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithZeroPrice)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithNullPrice)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenDurationIsInvalid() throws Exception {
    ServiceDTO serviceDTOWithNegativeDuration = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        Duration.ofHours(-1),
        VALID_SERVICE_DTO.workSpaceName()
    );
    ServiceDTO serviceDtoWithNullDuration = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        null,
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDTOWithNegativeDuration)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithNullDuration)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_returnsBadRequest_whenWorkSpaceNameIsInvalid() throws Exception {
    String invalidWorkSpaceName = "Invalid WorkSpace Name";
    ServiceDTO serviceDtoWithEmptyWorkSpace = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        ""
    );
    ServiceDTO serviceDtoWithInvalidName = new ServiceDTO(
        VALID_SERVICE_DTO.name(),
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        invalidWorkSpaceName
    );

    when(workSpaceRepository.existsByName(invalidWorkSpaceName)).thenReturn(false);

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithEmptyWorkSpace)))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post("/api/v1/services")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(serviceDtoWithInvalidName)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsOk_whenAssignmentIsSuccessful() throws Exception {
    ServiceView serviceView = mock(ServiceView.class);
    User user = mock(User.class);

    when(userService.findByEmail(VALID_STAFF_EMAIL)).thenReturn(user);
    when(user.getRole()).thenReturn(UserRoles.EMPLOYEE);
    when(serviceService.assignStaff(VALID_SERVICE_ID, VALID_STAFF_EMAIL)).thenReturn(serviceView);

    mockMvc.perform(post("/api/v1/services/{id}/assign", VALID_SERVICE_ID)
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void assignStaff_returnsBadRequest_whenEmailIsNotStaffOrAdministrator() throws Exception {
    String clientEmail = "client@example.com";
    User user = mock(User.class);

    when(userService.findByEmail(clientEmail)).thenReturn(user);
    when(user.getRole()).thenReturn(UserRoles.CLIENT);

    mockMvc.perform(post("/api/v1/services/{id}/assign", VALID_SERVICE_ID)
            .param("staffEmail", clientEmail))
        .andExpect(status().isBadRequest());
  }

  @Test
  void assignStaff_returnsNotFound_whenServiceNotFound() throws Exception {
    User user = mock(User.class);

    when(userService.findByEmail(VALID_STAFF_EMAIL)).thenReturn(user);
    when(user.getRole()).thenReturn(UserRoles.EMPLOYEE);
    when(serviceService.assignStaff(INVALID_SERVICE_ID, VALID_STAFF_EMAIL)).thenThrow(
        new NotFoundException(Exceptions.SERVICE_NOT_FOUND));

    mockMvc.perform(post("/api/v1/services/{id}/assign", INVALID_SERVICE_ID)
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isNotFound());
  }

  @Test
  void assignStaff_returnsNotFound_whenStaffEmailNotFound() throws Exception {
    String notExistingEmail = "nonexistent@example.com";
    User user = mock(User.class);

    when(userService.findByEmail(notExistingEmail)).thenReturn(user);
    when(user.getRole()).thenReturn(UserRoles.EMPLOYEE);
    when(serviceService.assignStaff(VALID_SERVICE_ID, notExistingEmail)).thenThrow(
        new NotFoundException(Exceptions.USER_NOT_FOUND));

    mockMvc.perform(post("/api/v1/services/{id}/assign", VALID_SERVICE_ID)
            .param("staffEmail", notExistingEmail))
        .andExpect(status().isNotFound());
  }

  @Test
  void unassignStaff_returnsOk_whenUnassignmentIsSuccessful() throws Exception {
    ServiceView serviceView = mock(ServiceView.class);

    when(serviceService.unassignStaff(VALID_SERVICE_ID, VALID_STAFF_EMAIL)).thenReturn(serviceView);

    mockMvc.perform(put("/api/v1/services/{id}/unassign", VALID_SERVICE_ID)
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void unassignStaff_returnsNotFound_whenServiceNotFound() throws Exception {
    when(serviceService.unassignStaff(INVALID_SERVICE_ID, VALID_STAFF_EMAIL))
        .thenThrow(new NotFoundException(Exceptions.SERVICE_NOT_FOUND));

    mockMvc.perform(put("/api/v1/services/{id}/unassign", INVALID_SERVICE_ID)
            .param("staffEmail", VALID_STAFF_EMAIL))
        .andExpect(status().isNotFound());
  }

  @Test
  void unassignStaff_returnsNotFound_whenStaffEmailNotFound() throws Exception {
    String notExistingEmail = "nonexistent@example.com";

    when(serviceService.unassignStaff(VALID_SERVICE_ID, notExistingEmail))
        .thenThrow(new NotFoundException(Exceptions.USER_NOT_FOUND));

    mockMvc.perform(put("/api/v1/services/{id}/unassign", VALID_SERVICE_ID)
            .param("staffEmail", notExistingEmail))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_returnsOk_whenUpdateIsSuccessful() throws Exception {
    ServiceView serviceView = mock(ServiceView.class);

    when(workSpaceRepository.existsByName(VALID_SERVICE_DTO.workSpaceName())).thenReturn(true);
    when(serviceService.update(VALID_SERVICE_ID, VALID_SERVICE_DTO)).thenReturn(serviceView);

    mockMvc.perform(put("/api/v1/services/{id}", VALID_SERVICE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void update_returnsBadRequest_whenRequestIsInvalid() throws Exception {
    ServiceDTO invalidServiceDTO = new ServiceDTO(
        "",
        VALID_SERVICE_DTO.description(),
        VALID_SERVICE_DTO.availability(),
        VALID_SERVICE_DTO.price(),
        VALID_SERVICE_DTO.duration(),
        VALID_SERVICE_DTO.workSpaceName()
    );

    mockMvc.perform(put("/api/v1/services/{id}", VALID_SERVICE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidServiceDTO)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returnsNotFound_whenServiceNotFound() throws Exception {
    when(workSpaceRepository.existsByName(VALID_SERVICE_DTO.workSpaceName())).thenReturn(true);
    when(serviceService.update(INVALID_SERVICE_ID, VALID_SERVICE_DTO)).thenThrow(
        new NotFoundException(Exceptions.SERVICE_NOT_FOUND));

    mockMvc.perform(put("/api/v1/services/{id}", INVALID_SERVICE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(VALID_SERVICE_DTO)))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_returnsNoContent_whenDeletionIsSuccessful() throws Exception {
    mockMvc.perform(delete("/api/v1/services/{id}", VALID_SERVICE_ID))
        .andExpect(status().isNoContent());
  }

  @Test
  void delete_returnsNotFound_whenServiceNotFound() throws Exception {
    doThrow(new NotFoundException(Exceptions.SERVICE_NOT_FOUND))
        .when(serviceService).delete(INVALID_SERVICE_ID);

    mockMvc.perform(delete("/api/v1/services/{id}", INVALID_SERVICE_ID))
        .andExpect(status().isNotFound());
  }
}