package com.internship.flow_appointment_scheduling.seed;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("development")
@RequiredArgsConstructor
@Order(3)
public class AppointmentSeedRunner implements ApplicationRunner {

  private final AppointmentRepository appointmentRepository;
  private final UserRepository userRepository;
  private final ServiceRepository serviceRepository;

  @Override
  public void run(ApplicationArguments args) {
    if (appointmentRepository.count() < 50) {
      seedAppointments();
    }
  }

  private void seedAppointments() {
    List<User> staffs = userRepository.findAllByRole(UserRoles.EMPLOYEE);
    List<User> clients = userRepository.findAllByRole(UserRoles.CLIENT);
    List<Service> services = serviceRepository.findAll();
    Random random = new Random();

    LocalDateTime currentDateTime = LocalDateTime.now();
    int appointmentsCount = 0;

    while (appointmentsCount < 50) {
      User client = clients.get(random.nextInt(clients.size()));
      User staff = staffs.get(random.nextInt(staffs.size()));
      Service service = services.get(random.nextInt(services.size()));

      StaffDetails staffDetails = staff.getStaffDetails();
      LocalTime startWorkingHour = staffDetails.getBeginWorkingHour();
      LocalTime endWorkingHour = staffDetails.getEndWorkingHour();

      LocalDateTime startDateTime = currentDateTime.plusDays(1)
          .withHour(startWorkingHour.getHour())
          .withMinute(startWorkingHour.getMinute());

      LocalDateTime endDateTime = startDateTime
          .plusMinutes(service.getDuration().toMinutes());

      if (startDateTime.toLocalTime().isBefore(startWorkingHour) || startDateTime.toLocalTime()
          .isAfter(endWorkingHour)) {
        currentDateTime = currentDateTime.plusDays(1)
            .withHour(startWorkingHour.getHour())
            .withMinute(startWorkingHour.getMinute());

        startDateTime = currentDateTime;
        endDateTime = startDateTime.plusMinutes(90);
      }

      Appointment appointment = Appointment.builder()
          .client(client)
          .staff(staff)
          .service(service)
          .startDate(startDateTime)
          .endDate(endDateTime)
          .status(AppointmentStatus.NOT_APPROVED)
          .build();

      appointmentRepository.save(appointment);

      currentDateTime = endDateTime;
      appointmentsCount++;
    }
  }
}
