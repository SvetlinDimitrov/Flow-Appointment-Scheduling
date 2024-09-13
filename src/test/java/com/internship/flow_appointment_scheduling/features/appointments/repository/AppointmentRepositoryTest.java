package com.internship.flow_appointment_scheduling.features.appointments.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.internship.flow_appointment_scheduling.FlowAppointmentSchedulingApplication;
import com.internship.flow_appointment_scheduling.features.appointments.config.TestContainersConfig;
import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.seed.AppointmentSeedRunner;
import com.internship.flow_appointment_scheduling.seed.ServiceSeedRunner;
import com.internship.flow_appointment_scheduling.seed.UserSeedRunner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserSeedRunner.class, AppointmentSeedRunner.class, ServiceSeedRunner.class})
@ContextConfiguration(classes = {TestContainersConfig.class,
    FlowAppointmentSchedulingApplication.class})
class AppointmentRepositoryTest {

  @Autowired
  private AppointmentRepository appointmentRepository;
  @Autowired
  private ServiceRepository serviceRepository;
  @Autowired
  private UserRepository userRepository;

  @Test
  void findAllByServiceId_ShouldReturnEmptyPage_WhenServiceDoesNotExits() {
    Service existingService = extractServiceWithExistingAppointments();
    serviceRepository.delete(existingService);
    Pageable pageable = PageRequest.of(0, 5);

    Page<Appointment> appointments = appointmentRepository.findAllByServiceId(
        existingService.getId(), pageable);

    assertThat(appointments.getContent()).hasSize(0);
  }

  @Test
  void findAllByServiceId_ShouldReturnNotEmptyPage_WhenServiceExitsWithData() {
    Service existingService = extractServiceWithExistingAppointments();
    Pageable pageable = PageRequest.of(0, 5);

    Page<Appointment> appointments = appointmentRepository.findAllByServiceId(
        existingService.getId(), pageable);

    assertThat(appointments.getTotalElements()).isEqualTo(existingService.getAppointments().size());
  }

  @Test
  void findAllByUserId_ShouldReturnEmptyPage_WhenUserDoesNotExits() {
    User existingUser = extractUserWithAppointmentsPreparedForDelete();
    userRepository.delete(existingUser);
    Pageable pageable = PageRequest.of(0, 5);

    Page<Appointment> appointments = appointmentRepository.findAllByUserId(
        existingUser.getId(), pageable);

    assertThat(appointments.getContent()).hasSize(0);
  }

  @Test
  void findAllByUserId_ShouldReturnNotEmptyPage_WhenUserExitsWithData() {
    User existingUser = extractUserWithAppointments();
    Pageable pageable = PageRequest.of(0, 5);
    int expectedCount =
        existingUser.getStaffAppointments().size() + existingUser.getClientAppointments().size();

    Page<Appointment> appointments = appointmentRepository.findAllByUserId(
        existingUser.getId(), pageable);

    assertThat(appointments.getTotalElements()).isEqualTo(expectedCount);
  }

  @Test
  void findAllByUserIdAndDate_ShouldReturnEmptyList_WhenUserDoesNotExits() {
    User existingUser = extractUserWithAppointmentsPreparedForDelete();
    userRepository.delete(existingUser);
    LocalDate date = LocalDate.of(2024, 9, 1);

    List<Appointment> appointments = appointmentRepository.findAllByUserIdAndDate(
        existingUser.getId(), date);

    assertThat(appointments.size()).isEqualTo(0);
  }

  @Test
  void findAllByUserIdAndDate_ShouldReturnNotEmptyList_WhenUserExitsWithData() {
    User existingUser = extractUserWithAppointments();
    LocalDate existingDate = null;
    if (!existingUser.getStaffAppointments().isEmpty()) {
      existingDate = extractLocalDateFromAppointments(existingUser.getStaffAppointments());
    } else {
      existingDate = extractLocalDateFromAppointments(existingUser.getClientAppointments());
    }

    List<Appointment> appointments = appointmentRepository.findAllByUserIdAndDate(
        existingUser.getId(), existingDate);

    assertThat(appointments.size()).isNotEqualTo(0);
  }

  @Test
  void findAllByUserIdAndDate_ShouldReturnEmptyList_WhenUserExitsButNoDataForDate() {
    User existingUser = extractUserWithAppointments();
    LocalDate date = LocalDate.of(1234, 9, 1);

    List<Appointment> appointments = appointmentRepository.findAllByUserIdAndDate(
        existingUser.getId(), date);

    assertThat(appointments.size()).isEqualTo(0);
  }

  @Test
  void findAllByUserIdAndDate_ShouldReturnNotEmptyList_WhenSpecificLocalDateIsProvided() {
    User existingUser = extractUserWithAppointments();
    LocalDate existingDate = null;
    int expectedCount = 0;
    if (!existingUser.getStaffAppointments().isEmpty()) {
      existingDate = extractLocalDateFromAppointments(existingUser.getStaffAppointments());
      expectedCount = extractTotalAppointmentsCountForSpecificLocalDate(
          existingUser.getStaffAppointments(), existingDate);
    } else {
      existingDate = extractLocalDateFromAppointments(existingUser.getClientAppointments());
      expectedCount = extractTotalAppointmentsCountForSpecificLocalDate(
          existingUser.getClientAppointments(), existingDate);
    }

    List<Appointment> appointments = appointmentRepository.findAllByUserIdAndDate(
        existingUser.getId(), existingDate);

    assertThat(appointments.size()).isEqualTo(expectedCount);
  }

  @Test
  void findAllByServiceIdAndDate_ShouldReturnEmptyList_WhenServiceDoesNotExits() {
    Service existingService = extractServiceWithExistingAppointments();
    serviceRepository.delete(existingService);
    LocalDate date = LocalDate.of(2024, 9, 1);

    List<Appointment> appointments = appointmentRepository.findAllByServiceIdAndDate(
        existingService.getId(), date);

    assertThat(appointments.size()).isEqualTo(0);
  }

  @Test
  void findAllByServiceIdAndDate_ShouldReturnNotEmptyList_WhenServiceExitsWithData() {
    Service existingService = extractServiceWithExistingAppointments();
    LocalDate existingDate = null;
    if (!existingService.getAppointments().isEmpty()) {
      existingDate = extractLocalDateFromAppointments(existingService.getAppointments());
    }

    List<Appointment> appointments = appointmentRepository.findAllByServiceIdAndDate(
        existingService.getId(), existingDate);

    assertThat(appointments.size()).isNotEqualTo(0);
  }

  @Test
  void findAllByServiceIdAndDate_ShouldReturnEmptyList_WhenServiceExitsButNoDataForDate() {
    Service existingService = extractServiceWithExistingAppointments();
    LocalDate invalidLocalDate = LocalDate.of(1234, 9, 1);

    List<Appointment> appointments = appointmentRepository.findAllByServiceIdAndDate(
        existingService.getId(), invalidLocalDate);

    assertThat(appointments.size()).isEqualTo(0);
  }

  @Test
  void findAllByServiceIdAndDate_ShouldReturnNotEmptyList_WhenSpecificLocalDateIsProvided() {
    Service existingService = extractServiceWithExistingAppointments();
    LocalDate existingDate = extractLocalDateFromAppointments(existingService.getAppointments());
    int expectedCount = extractTotalAppointmentsCountForSpecificLocalDate(
        existingService.getAppointments(), existingDate);

    List<Appointment> appointments = appointmentRepository.findAllByServiceIdAndDate(
        existingService.getId(), existingDate);

    assertThat(appointments.size()).isEqualTo(expectedCount);
  }

  @Test
  void existsOverlappingAppointment_ShouldReturnFalse_WhenNoOverlappingAppointmentExists() {
    User existingUser = extractUserWithAppointments();
    LocalDateTime startDate = LocalDateTime.of(1024, 9, 1, 10, 0);
    LocalDateTime endDate = startDate.plusMinutes(60);

    boolean isOverlapping = appointmentRepository.existsOverlappingAppointment(
        existingUser.getEmail(), startDate, endDate);

    assertThat(isOverlapping).isFalse();
  }

  @Test
  void existsOverlappingAppointment_ShouldReturnTrue_WhenOverlappingAppointmentExists() {
    User existingUser = extractUserWithAppointments();
    Appointment existingAppointment = null;
    if (!existingUser.getStaffAppointments().isEmpty()) {
      existingAppointment = existingUser.getStaffAppointments().getFirst();
    } else {
      existingAppointment = existingUser.getClientAppointments().getFirst();
    }
    LocalDateTime alreadyExistingStartDate = existingAppointment.getStartDate();
    LocalDateTime endDate = alreadyExistingStartDate.plusMinutes(120);

    boolean isOverlapping = appointmentRepository.existsOverlappingAppointment(
        existingUser.getEmail(), alreadyExistingStartDate, endDate);

    assertThat(isOverlapping).isTrue();
  }

  @Test
  void countAppointmentsInWorkspace_ShouldReturnZero_WhenNoAppointmentsExist() {
    Service existingService = extractServiceWithExistingAppointments();
    LocalDateTime invalidLocalDateTime = LocalDateTime.of(1024, 9, 1, 10, 0);
    LocalDateTime endDate = invalidLocalDateTime.plusMinutes(60);

    int count = appointmentRepository.countAppointmentsInWorkspace(
        existingService.getWorkSpace().getId(), invalidLocalDateTime, endDate);

    assertThat(count).isEqualTo(0);
  }

  @Test
  void countAppointmentsInWorkspace_ShouldReturnNonZero_WhenAppointmentsExist() {
    Service existingService = extractServiceWithExistingAppointments();
    LocalDateTime existingStartDate = existingService.getAppointments().getFirst().getStartDate();
    LocalDateTime endDate = existingStartDate.plusMinutes(60);

    int count = appointmentRepository.countAppointmentsInWorkspace(
        existingService.getWorkSpace().getId(), existingStartDate, endDate);

    assertThat(count).isNotEqualTo(0);
  }

  private Service extractServiceWithExistingAppointments() {
    return serviceRepository.findAll()
        .stream()
        .filter(service -> !service.getAppointments().isEmpty())
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No service with appointments found"));
  }

  private User extractUserWithAppointments() {
    return userRepository.findAll()
        .stream()
        .filter(user -> !user.getStaffAppointments().isEmpty()
            || !user.getClientAppointments().isEmpty())
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No user with appointments found"));
  }

  private User extractUserWithAppointmentsPreparedForDelete() {
    User existingUser = userRepository.findAll()
        .stream()
        .filter(user -> !user.getStaffAppointments().isEmpty()
            || !user.getClientAppointments().isEmpty())
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No user with appointments found"));

    List<Service> services = existingUser.getServices();
    services.forEach(service -> service.getUsers().remove(existingUser));
    serviceRepository.saveAll(services);

    return existingUser;
  }

  private int extractTotalAppointmentsCountForSpecificLocalDate(List<Appointment> appointments,
      LocalDate date) {
    return appointments
        .stream()
        .filter(appointment -> appointment.getStartDate().toLocalDate().equals(date))
        .toList()
        .size();
  }

  private LocalDate extractLocalDateFromAppointments(List<Appointment> appointments) {
    return appointments.getFirst().getStartDate().toLocalDate();
  }
}