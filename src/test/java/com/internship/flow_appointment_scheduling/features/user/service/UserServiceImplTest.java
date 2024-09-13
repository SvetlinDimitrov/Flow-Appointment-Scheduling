package com.internship.flow_appointment_scheduling.features.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.service.AppointmentService;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffDetailsDto;
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
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.StaffDetailsMapper;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.UserMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private AppointmentService appointmentService;
  @Mock
  private UserMapper userMapper;
  @Mock
  private StaffDetailsMapper staffDetailsMapper;

  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
    userService = new UserServiceImpl(
        userRepository,
        userMapper,
        staffDetailsMapper
    );

    userService.setAppointmentService(appointmentService);
  }

  @Test
  void getAll_returnsPageUsers_whenRoleIsProvided() {
    Pageable pageable = PageRequest.of(0, 10);
    UserRoles userRole = UserRoles.ADMINISTRATOR;
    User user = mock(User.class);
    UserView userView = mock(UserView.class);
    Page<User> userPage = new PageImpl<>(List.of(user));
    Page<UserView> userViewPage = new PageImpl<>(List.of(userView));

    when(userRepository.findAllByRole(userRole, pageable)).thenReturn(userPage);
    when(userMapper.toView(user)).thenReturn(userView);

    Page<UserView> result = userService.getAll(pageable, userRole);

    assertEquals(userViewPage, result);
    verify(userRepository).findAllByRole(userRole, pageable);
    verify(userMapper).toView(user);
  }

  @Test
  void getAll_returnsAllUsers_whenRoleIsNotProvided() {
    Pageable pageable = PageRequest.of(0, 10);
    User user = mock(User.class);
    UserView userView = mock(UserView.class);
    Page<User> userPage = new PageImpl<>(List.of(user));
    Page<UserView> userViewPage = new PageImpl<>(List.of(userView));

    when(userRepository.findAll(pageable)).thenReturn(userPage);
    when(userMapper.toView(user)).thenReturn(userView);

    Page<UserView> result = userService.getAll(pageable, null);

    assertEquals(userViewPage, result);
    verify(userRepository).findAll(pageable);
    verify(userMapper).toView(user);
  }

  @Test
  void getAllByServiceId_returnsPageUsers_whenServiceIdIsProvided() {
    Pageable pageable = PageRequest.of(0, 10);
    Long serviceId = 1L;
    User user = mock(User.class);
    UserView userView = mock(UserView.class);
    Page<User> userPage = new PageImpl<>(List.of(user));
    Page<UserView> userViewPage = new PageImpl<>(List.of(userView));

    when(userRepository.findAllByServiceId(serviceId, pageable)).thenReturn(userPage);
    when(userMapper.toView(user)).thenReturn(userView);

    Page<UserView> result = userService.getAllByServiceId(pageable, serviceId);

    assertEquals(userViewPage, result);
    verify(userRepository).findAllByServiceId(serviceId, pageable);
    verify(userMapper).toView(user);
  }

  @Test
  void getById_returnsUserView_whenUserExists() {
    Long userId = 1L;
    User user = mock(User.class);
    UserView userView = mock(UserView.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.toView(user)).thenReturn(userView);

    UserView result = userService.getById(userId);

    assertEquals(userView, result);
    verify(userRepository).findById(userId);
    verify(userMapper).toView(user);
  }

  @Test
  void getById_throwsNotFoundException_whenUserDoesNotExist() {
    Long userId = 1L;

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.getById(userId));
    verify(userRepository).findById(userId);
  }

  @Test
  void create_returnsUserView_whenUserIsCreated() {
    UserPostRequest createDto = mock(UserPostRequest.class);
    User userToSave = mock(User.class);
    User savedUser = mock(User.class);
    UserView userView = mock(UserView.class);

    when(userMapper.toEntity(createDto)).thenReturn(userToSave);
    when(userRepository.save(userToSave)).thenReturn(savedUser);
    when(userMapper.toView(savedUser)).thenReturn(userView);

    UserView result = userService.create(createDto);

    assertEquals(userView, result);
    verify(userMapper).toEntity(createDto);
    verify(userRepository).save(userToSave);
    verify(userMapper).toView(savedUser);
  }

  @Test
  void update_returnsUpdatedUserView_whenUserExists() {
    Long userId = 1L;
    UserPutRequest putDto = mock(UserPutRequest.class);
    User user = mock(User.class);
    User updatedUser = mock(User.class);
    UserView userView = mock(UserView.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(updatedUser);
    when(userMapper.toView(updatedUser)).thenReturn(userView);

    UserView result = userService.update(userId, putDto);

    assertEquals(userView, result);
    verify(userRepository).findById(userId);
    verify(userMapper).updateEntity(user, putDto);
    verify(userRepository).save(user);
    verify(userMapper).toView(updatedUser);
  }

  @Test
  void update_throwsNotFoundException_whenUserDoesNotExist() {
    Long userId = 1L;
    UserPutRequest putDto = mock(UserPutRequest.class);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.update(userId, putDto));
    verify(userRepository).findById(userId);
  }

  @Test
  void delete_deletesUserAndCancelsAppointments_whenUserExists() {
    Long userId = 1L;
    User user = mock(User.class);
    Appointment appointment1 = mock(Appointment.class);
    Appointment appointment2 = mock(Appointment.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(user.getStaffAppointments()).thenReturn(List.of(appointment1, appointment2));
    when(appointment1.getStatus()).thenReturn(AppointmentStatus.NOT_APPROVED);
    when(appointment2.getStatus()).thenReturn(AppointmentStatus.APPROVED);
    when(appointment1.getId()).thenReturn(1L);
    when(appointment2.getId()).thenReturn(2L);

    userService.delete(userId);

    verify(appointmentService).cancelAppointment(appointment1.getId());
    verify(appointmentService).cancelAppointment(appointment2.getId());
    verify(userRepository).delete(user);
  }

  @Test
  void delete_throwsNotFoundException_whenUserDoesNotExist() {
    Long userId = 1L;

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.delete(userId));
    verify(userRepository).findById(userId);
  }

  @Test
  void resetPassword_returnsUpdatedUserView_whenEmailIsValid() {
    String email = "test@example.com";
    UserPasswordUpdate dto = mock(UserPasswordUpdate.class);
    User user = mock(User.class);
    UserView userView = mock(UserView.class);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toView(user)).thenReturn(userView);

    UserView result = userService.resetPassword(email, dto);

    assertEquals(userView, result);
    verify(userRepository).findByEmail(email);
    verify(userMapper).updateEntity(user, dto);
    verify(userRepository).save(user);
  }

  @Test
  void resetPassword_throwsNotFoundException_whenEmailIsInvalid() {
    String email = "invalid@example.com";
    UserPasswordUpdate dto = mock(UserPasswordUpdate.class);

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.resetPassword(email, dto));
    verify(userRepository).findByEmail(email);
  }

  @Test
  void findByEmail_returnsUser_whenEmailIsValid() {
    String email = "test@example.com";
    User user = mock(User.class);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    User result = userService.findByEmail(email);

    assertEquals(user, result);
    verify(userRepository).findByEmail(email);
  }

  @Test
  void findByEmail_throwsNotFoundException_whenEmailIsInvalid() {
    String email = "invalid@example.com";

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.findByEmail(email));
    verify(userRepository).findByEmail(email);
  }

  @Test
  void hireStaff_returnsUserView_whenStaffIsHired() {
    StaffHireDto dto = mock(StaffHireDto.class);
    UserPostRequest userInfo = mock(UserPostRequest.class);
    StaffDetailsDto staffDetailsDto = mock(StaffDetailsDto.class);
    User staffToSave = mock(User.class);
    StaffDetails staffDetails = mock(StaffDetails.class);
    UserView userView = mock(UserView.class);

    when(dto.userInfo()).thenReturn(userInfo);
    when(dto.staffDetailsDto()).thenReturn(staffDetailsDto);
    when(userMapper.toEntity(userInfo)).thenReturn(staffToSave);
    when(staffDetailsMapper.toEntity(staffDetailsDto)).thenReturn(staffDetails);
    when(userRepository.save(staffToSave)).thenReturn(staffToSave);
    when(userMapper.toView(staffToSave)).thenReturn(userView);

    UserView result = userService.hireStaff(dto);

    assertEquals(userView, result);
    verify(userMapper).toEntity(userInfo);
    verify(staffDetailsMapper).toEntity(staffDetailsDto);
    verify(staffToSave).setRole(UserRoles.EMPLOYEE);
    verify(userRepository).save(staffToSave);
    verify(userMapper).toView(staffToSave);
  }

  @Test
  void modifyStaff_updatesStaffDetails_whenStaffIsValid() {
    Long staffId = 1L;
    StaffModifyDto dto = mock(StaffModifyDto.class);
    User staff = mock(User.class);
    StaffDetails staffDetails = mock(StaffDetails.class);
    UserView userView = mock(UserView.class);

    when(userRepository.findById(staffId)).thenReturn(Optional.of(staff));
    when(staff.getStaffDetails()).thenReturn(staffDetails);
    when(staffDetails.getIsAvailable()).thenReturn(false);
    when(staff.getRole()).thenReturn(UserRoles.EMPLOYEE);
    when(userRepository.save(staff)).thenReturn(staff);
    when(userMapper.toView(staff)).thenReturn(userView);

    UserView result = userService.modifyStaff(staffId, dto);

    assertEquals(userView, result);
    verify(userRepository).findById(staffId);
    verify(staffDetailsMapper).updateEntity(staffDetails, dto);
    verify(userRepository).save(staff);
    verify(userMapper).toView(staff);
  }

  @Test
  void modifyStaff_cancelsAppointments_whenStaffBecomesUnavailable() {
    Long staffId = 1L;
    StaffModifyDto dto = mock(StaffModifyDto.class);
    User staff = mock(User.class);
    StaffDetails staffDetails = mock(StaffDetails.class);
    Appointment appointment1 = mock(Appointment.class);
    Appointment appointment2 = mock(Appointment.class);
    UserView userView = mock(UserView.class);

    when(userRepository.findById(staffId)).thenReturn(Optional.of(staff));
    when(staff.getStaffDetails()).thenReturn(staffDetails);
    when(staff.getRole()).thenReturn(UserRoles.EMPLOYEE);
    when(staff.getStaffAppointments()).thenReturn(List.of(appointment1, appointment2));
    when(appointment1.getStatus()).thenReturn(AppointmentStatus.NOT_APPROVED);
    when(appointment2.getStatus()).thenReturn(AppointmentStatus.APPROVED);
    when(appointment1.getId()).thenReturn(1L);
    when(appointment2.getId()).thenReturn(2L);
    when(staffDetails.getIsAvailable()).thenReturn(true);
    when(dto.isAvailable()).thenReturn(false);
    when(userRepository.save(staff)).thenReturn(staff);
    when(userMapper.toView(staff)).thenReturn(userView);

    UserView result = userService.modifyStaff(staffId, dto);

    assertEquals(userView, result);
    verify(appointmentService).cancelAppointment(appointment1.getId());
    verify(appointmentService).cancelAppointment(appointment2.getId());
    verify(userRepository).save(staff);
    verify(userMapper).toView(staff);
  }

  @Test
  void modifyStaff_throwsBadRequestException_whenUserIsNotStaff() {
    Long staffId = 1L;
    StaffModifyDto dto = mock(StaffModifyDto.class);
    User staff = mock(User.class);

    when(userRepository.findById(staffId)).thenReturn(Optional.of(staff));
    when(staff.getRole()).thenReturn(UserRoles.CLIENT);

    assertThrows(BadRequestException.class, () -> userService.modifyStaff(staffId, dto));
    verify(userRepository).findById(staffId);
  }

  @Test
  void handleCompletingTheAppointment_updatesStaffDetails() {
    Appointment appointment = mock(Appointment.class);
    User staff = mock(User.class);
    StaffDetails staffDetails = mock(StaffDetails.class);
    Service service = mock(Service.class);
    BigDecimal initialProfit = BigDecimal.valueOf(200);

    when(appointment.getStaff()).thenReturn(staff);
    when(staff.getStaffDetails()).thenReturn(staffDetails);
    when(appointment.getService()).thenReturn(service);
    when(service.getPrice()).thenReturn(BigDecimal.valueOf(100));
    when(staffDetails.getProfit()).thenReturn(initialProfit);
    when(staffDetails.getCompletedAppointments()).thenReturn(5);

    userService.handleCompletingTheAppointment(appointment);

    verify(staffDetails).setProfit(initialProfit.add(BigDecimal.valueOf(100)));
    verify(staffDetails).setCompletedAppointments(6);
    verify(userRepository).save(staff);
  }
}