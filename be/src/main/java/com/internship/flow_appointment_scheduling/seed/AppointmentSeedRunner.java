package com.internship.flow_appointment_scheduling.seed;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.seed.enums.SeededClientUsers;
import com.internship.flow_appointment_scheduling.seed.enums.SeededServices;
import com.internship.flow_appointment_scheduling.seed.enums.SeededStaffUsers;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"development", "test"})
@RequiredArgsConstructor
@Order(3)
@Slf4j
public class AppointmentSeedRunner implements ApplicationRunner {

  private static final Long APPOINTMENTS_COUNT = 800L;
  private static final Long MIN_APPOINTMENTS_COUNT = 200L;
  private final AppointmentRepository appointmentRepository;
  private final UserRepository userRepository;
  private final ServiceRepository serviceRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    seedAppointments();
  }

  private void seedAppointments() {
    if (appointmentRepository.count() > MIN_APPOINTMENTS_COUNT) {
      return;
    }
    List<SeededStaffUsers> staffUsers = Arrays.asList(SeededStaffUsers.values());
    List<SeededClientUsers> clientUsers = Arrays.asList(SeededClientUsers.values());
    SeededServices[] services = SeededServices.values();
    Random random = new Random();

    int appointmentsCount = 0;

    for (SeededStaffUsers staffEnum : staffUsers) {
      User staff = userRepository.findByEmail(staffEnum.getEmail()).orElseThrow();
      Service service = getRandomService(staff, random);
      User client = getRandomClient(clientUsers, random);

      LocalDateTime lastAppointmentEndDate = getLastAppointmentEndDate(staff, client, service);

      createAppointment(client, staff, service, lastAppointmentEndDate);
      appointmentsCount++;
    }

    for (SeededClientUsers clientEnum : clientUsers) {
      User client = userRepository.findByEmail(clientEnum.getEmail()).orElseThrow();
      User staff = getRandomStaff(staffUsers, random);
      Service service = getRandomService(staff, random);

      LocalDateTime lastAppointmentEndDate = getLastAppointmentEndDate(staff, client, service);

      createAppointment(client, staff, service, lastAppointmentEndDate);
      appointmentsCount++;
    }

    for (SeededServices serviceEnum : services) {
      Service service = getService(serviceEnum);
      User staff = getStaffForService(service);
      User client = getRandomClient(clientUsers, random);

      LocalDateTime lastAppointmentEndDate = getLastAppointmentEndDate(staff, client, service);

      createAppointment(client, staff, service, lastAppointmentEndDate);
      appointmentsCount++;
    }

    while (appointmentsCount < APPOINTMENTS_COUNT) {
      User client = getRandomClient(clientUsers, random);
      User staff = getRandomStaff(staffUsers, random);
      Service service = getRandomService(staff, random);

      LocalDateTime lastAppointmentEndDate = getLastAppointmentEndDate(staff, client, service);

      createAppointment(client, staff, service, lastAppointmentEndDate);
      appointmentsCount++;
    }

    log.info("Seeding process completed. Total appointments created: {}", appointmentsCount);
  }

  private User getRandomClient(List<SeededClientUsers> clientUsers, Random random) {
    return userRepository.findByEmail(
        clientUsers.get(random.nextInt(clientUsers.size())).getEmail()).orElseThrow();
  }

  private User getRandomStaff(List<SeededStaffUsers> staffUsers, Random random) {
    return userRepository.findByEmail(staffUsers.get(random.nextInt(staffUsers.size())).getEmail())
        .orElseThrow();
  }

  private Service getRandomService(User staff, Random random) {
    return staff.getServices().get(random.nextInt(staff.getServices().size()));
  }

  private Service getService(SeededServices serviceEnum) {
    return serviceRepository.findAllByName(serviceEnum.getName())
        .stream()
        .filter(s -> s.getDescription().equals(serviceEnum.getDescription()))
        .filter(s -> s.getDuration().equals(serviceEnum.getDuration()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Service not found with " + serviceEnum.getName()));
  }

  private User getStaffForService(Service service) {
    return userRepository.findAll().stream()
        .filter(user -> user.getServices().contains(service))
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException("Staff not found for service " + service.getName()));
  }

  private LocalDateTime getLastAppointmentEndDate(User staff, User client, Service service) {
    return Stream.concat(
            Stream.concat(
                staff.getStaffAppointments().stream().map(Appointment::getStartDate),
                client.getClientAppointments().stream().map(Appointment::getStartDate)
            ),
            service.getAppointments().stream().map(Appointment::getStartDate)
        )
        .max(LocalDateTime::compareTo)
        .orElse(LocalDateTime.now());
  }

  private void createAppointment(User client, User staff, Service service,
      LocalDateTime currentDateTime) {
    StaffDetails staffDetails = staff.getStaffDetails();
    LocalTime startWorkingHour = staffDetails.getBeginWorkingHour();
    LocalTime endWorkingHour = staffDetails.getEndWorkingHour();

    LocalDateTime startDateTime = currentDateTime.plusHours(1)
        .withHour(startWorkingHour.getHour())
        .withMinute(startWorkingHour.getMinute());

    LocalDateTime endDateTime = startDateTime.plusMinutes(service.getDuration().toMinutes());

    while (isOverlapping(staff, client, startDateTime, endDateTime) ||
        !isWorkspaceAvailable(service, startDateTime, endDateTime)) {
      startDateTime = startDateTime.plusMinutes(50);
      endDateTime = startDateTime.plusMinutes(service.getDuration().toMinutes());

      if (startDateTime.toLocalTime().isAfter(endWorkingHour) || endDateTime.toLocalTime()
          .isAfter(endWorkingHour)) {
        startDateTime = startDateTime.plusMinutes(30)
            .withHour(startDateTime.getHour())
            .withMinute(startDateTime.getMinute());
        endDateTime = startDateTime.plusMinutes(service.getDuration().toMinutes());
      }
    }

    AppointmentStatus[] statuses = Arrays.stream(AppointmentStatus.values())
        .filter(status -> status != AppointmentStatus.COMPLETED)
        .toArray(AppointmentStatus[]::new);

    Random random = new Random();
    AppointmentStatus randomStatus = statuses[random.nextInt(statuses.length)];

    Appointment appointment = Appointment.builder()
        .client(client)
        .staff(staff)
        .service(service)
        .startDate(startDateTime)
        .endDate(endDateTime)
        .status(randomStatus)
        .build();

    Appointment savedAppointment = appointmentRepository.save(appointment);
    client.getClientAppointments().add(savedAppointment);
    staff.getStaffAppointments().add(savedAppointment);
    service.getAppointments().add(savedAppointment);

    userRepository.save(client);
    userRepository.save(staff);
    serviceRepository.save(service);
  }

  private boolean isWorkspaceAvailable(Service service, LocalDateTime startDateTime,
      LocalDateTime endDateTime) {
    long currentAppointments = service.getAppointments().stream()
        .filter(appointment ->
            appointment.getStartDate() != null && appointment.getEndDate() != null &&
                !appointment.getEndDate().isBefore(startDateTime) &&
                !appointment.getStartDate().isAfter(endDateTime) &&
                (appointment.getStatus() == AppointmentStatus.APPROVED
                    || appointment.getStatus() == AppointmentStatus.NOT_APPROVED)
        )
        .count();
    return currentAppointments < service.getWorkSpace().getAvailableSlots();
  }

  private boolean isOverlapping(User staff, User client, LocalDateTime startDateTime,
      LocalDateTime endDateTime) {
    boolean staffOverlap = staff.getStaffAppointments().stream()
        .anyMatch(appointment ->
            appointment.getStartDate() != null && appointment.getEndDate() != null &&
                !appointment.getEndDate().isBefore(startDateTime) &&
                !appointment.getStartDate().isAfter(endDateTime) &&
                (appointment.getStatus() == AppointmentStatus.APPROVED
                    || appointment.getStatus() == AppointmentStatus.NOT_APPROVED)
        );

    boolean clientOverlap = client.getClientAppointments().stream()
        .anyMatch(appointment ->
            appointment.getStartDate() != null && appointment.getEndDate() != null &&
                !appointment.getEndDate().isBefore(startDateTime) &&
                !appointment.getStartDate().isAfter(endDateTime) &&
                (appointment.getStatus() == AppointmentStatus.APPROVED
                    || appointment.getStatus() == AppointmentStatus.NOT_APPROVED)
        );

    return staffOverlap || clientOverlap;
  }
}