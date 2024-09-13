package com.internship.flow_appointment_scheduling.features.appointments.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.ShortAppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.enums.UpdateAppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.appointments.utils.AppointmentValidator;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.service.ServiceService;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.appointment.AppointmentMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

  @Mock
  private AppointmentRepository appointmentRepository;
  @Mock
  private UserService userService;
  @Mock
  private ServiceService serviceService;
  @Mock
  private AppointmentMapper appointmentMapper;
  @Mock
  private AppointmentValidator appointmentValidator;
  @Mock
  private ApplicationEventPublisher eventPublisher;

  private AppointmentServiceImpl appointmentServiceImplUnderTest;

  @BeforeEach
  void setUp() {
    appointmentServiceImplUnderTest = new AppointmentServiceImpl(
        appointmentRepository,
        appointmentMapper,
        appointmentValidator,
        eventPublisher
    );

    appointmentServiceImplUnderTest.setServiceService(serviceService);
    appointmentServiceImplUnderTest.setUserService(userService);
  }

  @Test
  void getAll_returnPageAppointments_whenValidPageableIsProvided() {
    Pageable pageable = PageRequest.of(0, 10);

    when(appointmentRepository.findAll(pageable)).thenReturn(Page.empty());

    appointmentServiceImplUnderTest.getAll(pageable);

    assertEquals(Page.empty(), appointmentRepository.findAll(pageable));
  }

  @Test
  void getAllByUserId_returnPageAppointments_whenValidPageableIsProvided() {
    Pageable pageable = PageRequest.of(0, 10);
    long VALID_USER_ID = 1;

    when(appointmentRepository.findAllByUserId(VALID_USER_ID, pageable)).thenReturn(Page.empty());

    Page<AppointmentView> result = appointmentServiceImplUnderTest.getAllByUserId(
        VALID_USER_ID, pageable);

    assertEquals(Page.empty(), result);
  }

  @Test
  void getAllByUserIdAndDate_returnListAppointments_whenValidUserIdAndDate() {
    long VALID_USER_ID = 1;
    LocalDate VALID_DATE = LocalDate.now();

    when(appointmentRepository.findAllByUserIdAndDate(VALID_USER_ID, VALID_DATE)).thenReturn(
        List.of());

    List<ShortAppointmentView> result = appointmentServiceImplUnderTest.getAllByUserIdAndDate(
        VALID_USER_ID, VALID_DATE);

    assertEquals(List.of(), result);
  }

  @Test
  void getAllByServiceIdAndDate_returnListAppointments_whenValidServiceIdAndDate() {
    long VALID_SERVICE_ID = 1;
    LocalDate VALID_DATE = LocalDate.now();

    when(appointmentRepository.findAllByServiceIdAndDate(VALID_SERVICE_ID, VALID_DATE)).thenReturn(
        List.of());

    List<ShortAppointmentView> result = appointmentServiceImplUnderTest.getAllByServiceIdAndDate(
        VALID_SERVICE_ID, VALID_DATE);

    assertEquals(List.of(), result);
  }

  @Test
  void getAllByServiceIdAndDate_returnPageAppointments_whenValidServiceIdAndPage() {
    long VALID_SERVICE_ID = 1;
    Pageable pageable = PageRequest.of(0, 10);

    when(appointmentRepository.findAllByServiceId(VALID_SERVICE_ID, pageable)).thenReturn(
        Page.empty());

    Page<AppointmentView> result = appointmentServiceImplUnderTest.getAllByServiceId(
        VALID_SERVICE_ID, pageable);

    assertEquals(Page.empty(), result);
  }

  @Test
  void getById_returnAppointment_whenValidIdIsProvided() {
    long VALID_ID = 1;
    Appointment mockAppointment = mock(Appointment.class);
    AppointmentView mockAppointmentView = mock(AppointmentView.class);

    when(appointmentRepository.findById(VALID_ID)).thenReturn(
        java.util.Optional.of(mockAppointment));
    when(appointmentMapper.toView(mockAppointment)).thenReturn(mockAppointmentView);

    AppointmentView result = appointmentServiceImplUnderTest.getById(VALID_ID);

    assertEquals(mockAppointmentView, result);
  }

  @Test
  void getById_throwException_whenInvalidIdIsProvided() {
    long INVALID_ID = 1;
    when(appointmentRepository.findById(INVALID_ID)).thenReturn(java.util.Optional.empty());

    Assertions.assertThrows(NotFoundException.class, () -> {
      appointmentServiceImplUnderTest.getById(INVALID_ID);
    });
  }

  @Test
  void create_throwException_whenInvalidClientEmail() {
    Long validServiceId = 1L;
    String invalidClientEmail = "client@abv.bg";
    String validStaffEmail = "staff@abv.bg";
    LocalDateTime validStart = LocalDateTime.now();
    AppointmentCreate mockAppointmentCreate = new AppointmentCreate(
        validServiceId, invalidClientEmail, validStaffEmail, validStart
    );

    when(userService.findByEmail(invalidClientEmail)).thenThrow(NotFoundException.class);

    Assertions.assertThrows(NotFoundException.class, () -> {
      appointmentServiceImplUnderTest.create(mockAppointmentCreate);
    });
  }

  @Test
  void create_throwException_whenInvalidStaffEmail() {
    Long validServiceId = 1L;
    String validClientEmail = "client@abv.bg";
    String invalidStaffEmail = "staff@abv.bg";
    LocalDateTime validStart = LocalDateTime.now();
    AppointmentCreate mockAppointmentCreate = new AppointmentCreate(
        validServiceId, validClientEmail, invalidStaffEmail, validStart
    );
    User mockUser = mock(User.class);

    when(userService.findByEmail(validClientEmail)).thenReturn(mockUser);
    when(userService.findByEmail(invalidStaffEmail)).thenThrow(NotFoundException.class);

    Assertions.assertThrows(NotFoundException.class, () -> {
      appointmentServiceImplUnderTest.create(mockAppointmentCreate);
    });
  }

  @Test
  void create_throwException_whenInvalidServiceId() {
    Long invalidServiceId = 1L;
    String validClientEmail = "client@abv.bg";
    String validStaffEmail = "staff@abv.bg";
    LocalDateTime validStart = LocalDateTime.now();
    AppointmentCreate mockAppointmentCreate = new AppointmentCreate(
        invalidServiceId, validClientEmail, validStaffEmail, validStart
    );
    User mockClient = mock(User.class);
    User mockStaff = mock(User.class);

    when(userService.findByEmail(validClientEmail)).thenReturn(mockClient);
    when(userService.findByEmail(validStaffEmail)).thenReturn(mockStaff);
    when(serviceService.findById(invalidServiceId)).thenThrow(NotFoundException.class);

    Assertions.assertThrows(NotFoundException.class, () -> {
      appointmentServiceImplUnderTest.create(mockAppointmentCreate);
    });
  }

  @Test
  void create_throwException_whenInvalidAppointmentValidation() {
    Long invalidServiceId = 1L;
    String validClientEmail = "client@abv.bg";
    String validStaffEmail = "staff@abv.bg";
    LocalDateTime validStart = LocalDateTime.now();
    LocalDateTime validEnd = validStart.plusMinutes(30);
    AppointmentCreate mockAppointmentCreate = new AppointmentCreate(
        invalidServiceId, validClientEmail, validStaffEmail, validStart
    );
    User mockClient = mock(User.class);
    User mockStaff = mock(User.class);
    Service mockService = mock(Service.class);
    Duration mockDuration = mock(Duration.class);

    when(userService.findByEmail(validClientEmail)).thenReturn(mockClient);
    when(userService.findByEmail(validStaffEmail)).thenReturn(mockStaff);
    when(serviceService.findById(invalidServiceId)).thenReturn(mockService);
    when(mockService.getDuration()).thenReturn(mockDuration);
    when(mockDuration.toMinutes()).thenReturn(30L);
    doThrow(BadRequestException.class)
        .when(appointmentValidator)
        .validateAppointment(mockStaff, mockClient, mockService, validStart, validEnd);

    Assertions.assertThrows(BadRequestException.class, () -> {
      appointmentServiceImplUnderTest.create(mockAppointmentCreate);
    });
  }

  @Test
  void create_createAppointment_whenValidDataIsProvided() {
    Long invalidServiceId = 1L;
    String validClientEmail = "client@abv.bg";
    String validStaffEmail = "staff@abv.bg";
    LocalDateTime validStart = LocalDateTime.now();
    AppointmentCreate mockAppointmentCreate = new AppointmentCreate(
        invalidServiceId, validClientEmail, validStaffEmail, validStart
    );
    User mockClient = mock(User.class);
    User mockStaff = mock(User.class);
    Service mockService = mock(Service.class);
    Duration mockDuration = mock(Duration.class);
    Appointment mockAppointment = mock(Appointment.class);
    AppointmentView mockAppointmentView = mock(AppointmentView.class);

    when(userService.findByEmail(validClientEmail)).thenReturn(mockClient);
    when(userService.findByEmail(validStaffEmail)).thenReturn(mockStaff);
    when(serviceService.findById(invalidServiceId)).thenReturn(mockService);
    when(mockService.getDuration()).thenReturn(mockDuration);
    when(mockDuration.toMinutes()).thenReturn(30L);
    when(appointmentMapper.toEntity(mockAppointmentCreate)).thenReturn(mockAppointment);
    when(appointmentRepository.save(mockAppointment)).thenReturn(mockAppointment);
    when(appointmentMapper.toView(mockAppointment)).thenReturn(mockAppointmentView);

    AppointmentView result = appointmentServiceImplUnderTest.create(mockAppointmentCreate);
    assertEquals(mockAppointmentView, result);
  }

  @Test
  void update_throwException_whenInvalidIdIsProvided() {
    long invalidId = 1;
    AppointmentUpdate dto = mock(AppointmentUpdate.class);

    when(appointmentRepository.findById(invalidId)).thenReturn(java.util.Optional.empty());

    Assertions.assertThrows(NotFoundException.class, () -> {
      appointmentServiceImplUnderTest.update(invalidId, dto);
    });
  }

  @Test
  void update_throwException_whenCancelingTheAppointmentAgain() {
    long validId = 1;
    AppointmentUpdate dto = mock(AppointmentUpdate.class);
    Appointment mockAppointment = mock(Appointment.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));
    when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.CANCELED);

    Assertions.assertThrows(BadRequestException.class, () -> {
      appointmentServiceImplUnderTest.update(validId, dto);
    });
  }

  @Test
  void update_throwException_whenProvidingStatusDifferentThanCancelToCompletedAppointment() {
    long validId = 1;
    AppointmentUpdate dto = mock(AppointmentUpdate.class);
    Appointment mockAppointment = mock(Appointment.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));
    when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.COMPLETED);
    when(dto.status()).thenReturn(UpdateAppointmentStatus.APPROVED);

    Assertions.assertThrows(BadRequestException.class, () -> {
      appointmentServiceImplUnderTest.update(validId, dto);
    });

    when(dto.status()).thenReturn(UpdateAppointmentStatus.COMPLETED);

    Assertions.assertThrows(BadRequestException.class, () -> {
      appointmentServiceImplUnderTest.update(validId, dto);
    });

  }

  @Test
  void update_returnAppointment_whenApprovingAgain() {
    long validId = 1;
    AppointmentUpdate dto = mock(AppointmentUpdate.class);
    Appointment mockAppointment = mock(Appointment.class);
    AppointmentView mockAppointmentView = mock(AppointmentView.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));
    when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.APPROVED);
    when(dto.status()).thenReturn(UpdateAppointmentStatus.APPROVED);
    when(appointmentMapper.toView(mockAppointment)).thenReturn(mockAppointmentView);

    AppointmentView result = appointmentServiceImplUnderTest.update(validId, dto);

    assertEquals(mockAppointmentView, result);
  }

  @Test
  void update_returnAppointment_whenCancelingTheAppointment() {
    long validId = 1;
    AppointmentUpdate dto = mock(AppointmentUpdate.class);
    Appointment mockAppointment = mock(Appointment.class);
    AppointmentView mockAppointmentView = mock(AppointmentView.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));
    when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.APPROVED);
    when(dto.status()).thenReturn(UpdateAppointmentStatus.CANCELED);
    when(appointmentRepository.save(mockAppointment)).thenReturn(mockAppointment);
    when(appointmentMapper.toView(mockAppointment)).thenReturn(mockAppointmentView);

    AppointmentView result = appointmentServiceImplUnderTest.update(validId, dto);

    assertEquals(mockAppointmentView, result);
  }

  @Test
  void update_returnAppointment_whenCompletingTheAppointment() {
    long validId = 1;
    AppointmentUpdate dto = mock(AppointmentUpdate.class);
    Appointment mockAppointment = mock(Appointment.class);
    AppointmentView mockAppointmentView = mock(AppointmentView.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));
    when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.APPROVED);
    when(dto.status()).thenReturn(UpdateAppointmentStatus.COMPLETED);
    when(appointmentRepository.save(mockAppointment)).thenReturn(mockAppointment);
    when(appointmentMapper.toView(mockAppointment)).thenReturn(mockAppointmentView);

    AppointmentView result = appointmentServiceImplUnderTest.update(validId, dto);

    assertEquals(mockAppointmentView, result);
  }

  @Test
  void update_returnAppointment_whenApprovingTheAppointment() {
    long validId = 1;
    AppointmentUpdate dto = mock(AppointmentUpdate.class);
    Appointment mockAppointment = mock(Appointment.class);
    AppointmentView mockAppointmentView = mock(AppointmentView.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));
    when(mockAppointment.getStatus()).thenReturn(AppointmentStatus.NOT_APPROVED);
    when(dto.status()).thenReturn(UpdateAppointmentStatus.APPROVED);
    when(appointmentRepository.save(mockAppointment)).thenReturn(mockAppointment);
    when(appointmentMapper.toView(mockAppointment)).thenReturn(mockAppointmentView);

    AppointmentView result = appointmentServiceImplUnderTest.update(validId, dto);

    assertEquals(mockAppointmentView, result);
  }

  @Test
  void delete_throwException_whenInvalidIdIsProvided() {
    long invalidId = 1;

    when(appointmentRepository.findById(invalidId)).thenReturn(Optional.empty());
    Assertions.assertThrows(NotFoundException.class, () -> {
      appointmentServiceImplUnderTest.delete(invalidId);
    });
  }

  @Test
  void delete_deleteAppointment_whenValidIdIsProvided() {
    long validId = 1;
    Appointment mockAppointment = mock(Appointment.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));
    appointmentServiceImplUnderTest.delete(validId);

    verify(appointmentRepository).delete(mockAppointment);
  }

  @Test
  void cancelAppointment_throwException_whenInvalidIdIsProvided() {
    long invalidId = 1;
    when(appointmentRepository.findById(invalidId)).thenReturn(Optional.empty());

    Assertions.assertThrows(NotFoundException.class, () -> {
      appointmentServiceImplUnderTest.cancelAppointment(invalidId);
    });
  }

  @Test
  void cancelAppointment_saveAppointment_whenValidIdIsProvided() {
    long validId = 1;
    Appointment mockAppointment = mock(Appointment.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));
    appointmentServiceImplUnderTest.cancelAppointment(validId);

    verify(appointmentRepository).save(mockAppointment);
  }

  @Test
  void completeAppointment_throwException_whenInvalidIdIsProvided() {
    long invalidId = 1;

    when(appointmentRepository.findById(invalidId)).thenReturn(Optional.empty());

    Assertions.assertThrows(NotFoundException.class, () -> {
      appointmentServiceImplUnderTest.completeAppointment(invalidId);
    });
  }

  @Test
  void completeAppointment_saveAppointment_whenValidIdIsProvided() {
    long validId = 1;
    Appointment mockAppointment = mock(Appointment.class);

    when(appointmentRepository.findById(validId)).thenReturn(Optional.of(mockAppointment));

    appointmentServiceImplUnderTest.completeAppointment(validId);

    verify(appointmentRepository).save(mockAppointment);
  }
}