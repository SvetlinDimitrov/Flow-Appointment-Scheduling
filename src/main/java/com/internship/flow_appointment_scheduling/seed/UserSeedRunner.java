package com.internship.flow_appointment_scheduling.seed;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("development")
@RequiredArgsConstructor
@Order(2)
public class UserSeedRunner implements ApplicationRunner {

  private final UserRepository userRepository;
  private final ServiceRepository serviceRepository;

  /**
   * The hashed password used for seeding users in the development environment. This hash
   * corresponds to the plaintext password "password123A!".
   */
  private final static String PASSWORD = "$2a$10$v3jjdP2RNNpea0Lfb/GzP.Ujj1S4aSzDxXT/vWT2XobBTzexNZmAm";

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    List<Service> allServices = serviceRepository.findAll();

    initAdministrators().forEach(admin -> {
          if (!userRepository.existsByEmail(admin.getEmail())) {
            userRepository.save(admin);
          }
        }
    );

    initStaffMembers().forEach(staff -> {
          if (!userRepository.existsByEmail(staff.getEmail())) {
            userRepository.save(staff);
            selectRandomServices(allServices, new Random()).forEach(service -> {
              service.getUsers().add(staff);
              serviceRepository.save(service);
            });
          }
        }
    );

    initClients().forEach(client -> {
      if (!userRepository.existsByEmail(client.getEmail())) {
        userRepository.save(client);
      }
    });
  }

  private List<User> initAdministrators() {
    User admin1 = User.builder()
        .email("admin1@flow.com")
        .password(PASSWORD)
        .firstName("George")
        .lastName("Smith")
        .role(UserRoles.ADMINISTRATOR)
        .services(new ArrayList<>())
        .build();
    StaffDetails admin1Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(10, 0))
        .endWorkingHour(LocalTime.of(18, 0))
        .completedAppointments(46)
        .profit(BigDecimal.valueOf(15000))
        .salary(BigDecimal.valueOf(2000))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 11, 1))
        .build();
    admin1Details.setUser(admin1);
    admin1.setStaffDetails(admin1Details);

    User admin2 = User.builder()
        .email("admin2@flow.com")
        .password(PASSWORD)
        .firstName("John")
        .lastName("Doe")
        .role(UserRoles.ADMINISTRATOR)
        .services(new ArrayList<>())
        .build();
    StaffDetails adminDetails2 = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(9, 0))
        .endWorkingHour(LocalTime.of(17, 0))
        .completedAppointments(30)
        .profit(BigDecimal.valueOf(10000))
        .salary(BigDecimal.valueOf(1800))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 10, 1))
        .build();
    adminDetails2.setUser(admin2);
    admin2.setStaffDetails(adminDetails2);

    User admin3 = User.builder()
        .email("admin3@flow.com")
        .password(PASSWORD)
        .firstName("Jane")
        .lastName("Doe")
        .role(UserRoles.ADMINISTRATOR)
        .services(new ArrayList<>())
        .build();
    StaffDetails admin3Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(8, 0))
        .endWorkingHour(LocalTime.of(16, 0))
        .completedAppointments(25)
        .profit(BigDecimal.valueOf(12000))
        .salary(BigDecimal.valueOf(1900))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 9, 1))
        .build();
    admin3Details.setUser(admin3);
    admin3.setStaffDetails(admin3Details);

    return Arrays.asList(admin1, admin2, admin3);
  }

  private List<User> initStaffMembers() {
    User staff1 = User.builder()
        .email("staff1@flow.com")
        .password(PASSWORD)
        .firstName("Alice")
        .lastName("Johnson")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff1Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(9, 0))
        .endWorkingHour(LocalTime.of(17, 0))
        .completedAppointments(20)
        .profit(BigDecimal.valueOf(5000))
        .salary(BigDecimal.valueOf(1500))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 1, 1))
        .build();
    staff1Details.setUser(staff1);
    staff1.setStaffDetails(staff1Details);

    User staff2 = User.builder()
        .email("staff2@flow.com")
        .password(PASSWORD)
        .firstName("Bob")
        .lastName("Williams")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff2Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(10, 0))
        .endWorkingHour(LocalTime.of(18, 0))
        .completedAppointments(25)
        .profit(BigDecimal.valueOf(6000))
        .salary(BigDecimal.valueOf(1600))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 2, 1))
        .build();
    staff2Details.setUser(staff2);
    staff2.setStaffDetails(staff2Details);

    User staff3 = User.builder()
        .email("staff3@flow.com")
        .password(PASSWORD)
        .firstName("Charlie")
        .lastName("Brown")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff3Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(8, 0))
        .endWorkingHour(LocalTime.of(16, 0))
        .completedAppointments(30)
        .profit(BigDecimal.valueOf(7000))
        .salary(BigDecimal.valueOf(1700))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 3, 1))
        .build();
    staff3Details.setUser(staff3);
    staff3.setStaffDetails(staff3Details);

    User staff4 = User.builder()
        .email("staff4@flow.com")
        .password(PASSWORD)
        .firstName("David")
        .lastName("Smith")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff4Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(9, 0))
        .endWorkingHour(LocalTime.of(17, 0))
        .completedAppointments(35)
        .profit(BigDecimal.valueOf(8000))
        .salary(BigDecimal.valueOf(1800))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 4, 1))
        .build();
    staff4Details.setUser(staff4);
    staff4.setStaffDetails(staff4Details);

    User staff5 = User.builder()
        .email("staff5@flow.com")
        .password(PASSWORD)
        .firstName("Eve")
        .lastName("Davis")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff5Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(10, 0))
        .endWorkingHour(LocalTime.of(18, 0))
        .completedAppointments(40)
        .profit(BigDecimal.valueOf(9000))
        .salary(BigDecimal.valueOf(1900))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 5, 1))
        .build();
    staff5Details.setUser(staff5);
    staff5.setStaffDetails(staff5Details);

    User staff6 = User.builder()
        .email("staff6@flow.com")
        .password(PASSWORD)
        .firstName("Frank")
        .lastName("Miller")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff6Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(8, 0))
        .endWorkingHour(LocalTime.of(16, 0))
        .completedAppointments(45)
        .profit(BigDecimal.valueOf(10000))
        .salary(BigDecimal.valueOf(2000))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 6, 1))
        .build();
    staff6Details.setUser(staff6);
    staff6.setStaffDetails(staff6Details);

    User staff7 = User.builder()
        .email("staff7@flow.com")
        .password(PASSWORD)
        .firstName("Grace")
        .lastName("Wilson")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff7Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(9, 0))
        .endWorkingHour(LocalTime.of(17, 0))
        .completedAppointments(50)
        .profit(BigDecimal.valueOf(11000))
        .salary(BigDecimal.valueOf(2100))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 7, 1))
        .build();
    staff7.setStaffDetails(staff7Details);
    staff7Details.setUser(staff7);

    User staff8 = User.builder()
        .email("staff8@flow.com")
        .password(PASSWORD)
        .firstName("Hank")
        .lastName("Moore")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff8Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(10, 0))
        .endWorkingHour(LocalTime.of(18, 0))
        .completedAppointments(55)
        .profit(BigDecimal.valueOf(12000))
        .salary(BigDecimal.valueOf(2200))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 8, 1))
        .build();
    staff8Details.setUser(staff8);
    staff8.setStaffDetails(staff8Details);

    User staff9 = User.builder()
        .email("staff9@flow.com")
        .password(PASSWORD)
        .firstName("Ivy")
        .lastName("Taylor")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff9Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(8, 0))
        .endWorkingHour(LocalTime.of(16, 0))
        .completedAppointments(60)
        .profit(BigDecimal.valueOf(13000))
        .salary(BigDecimal.valueOf(2300))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 9, 1))
        .build();
    staff9Details.setUser(staff9);
    staff9.setStaffDetails(staff9Details);

    User staff10 = User.builder()
        .email("staff10@flow.com")
        .password(PASSWORD)
        .firstName("Jack")
        .lastName("Anderson")
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();
    StaffDetails staff10Details = StaffDetails.builder()
        .beginWorkingHour(LocalTime.of(9, 0))
        .endWorkingHour(LocalTime.of(17, 0))
        .completedAppointments(65)
        .profit(BigDecimal.valueOf(14000))
        .salary(BigDecimal.valueOf(2400))
        .isAvailable(true)
        .startDate(LocalDate.of(2023, 10, 1))
        .build();
    staff10Details.setUser(staff10);
    staff10.setStaffDetails(staff10Details);

    return Arrays.asList(staff1, staff2, staff3, staff4, staff5, staff6, staff7, staff8, staff9,
        staff10);
  }

  private List<User> initClients() {
    List<User> clients = new ArrayList<>();

    List<String> firstNames = Arrays.asList(
        "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack",
        "Kathy", "Leo", "Mona", "Nina", "Oscar", "Paul", "Quincy", "Rita", "Steve", "Tina"
    );

    List<String> lastNames = Arrays.asList(
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez",
        "Wilson",
        "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin", "Jackson",
        "Thompson", "White"
    );

    for (int i = 1; i <= 20; i++) {
      User client = User.builder()
          .email("client" + i + "@abv.bg")
          .password(PASSWORD)
          .firstName(firstNames.get(i - 1))
          .lastName(lastNames.get(i - 1))
          .role(UserRoles.CLIENT)
          .build();
      clients.add(client);
    }

    return clients;
  }

  private List<Service> selectRandomServices(List<Service> allServices, Random random) {
    int numberOfServices = random.nextInt(4) + 1;
    Set<Service> selectedServices = new HashSet<>();
    while (selectedServices.size() < numberOfServices) {
      Service randomService = allServices.get(random.nextInt(allServices.size()));
      selectedServices.add(randomService);
    }
    return selectedServices.stream().toList();
  }
}
