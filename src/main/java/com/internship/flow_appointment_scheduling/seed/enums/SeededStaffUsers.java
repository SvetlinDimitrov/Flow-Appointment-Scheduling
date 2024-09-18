package com.internship.flow_appointment_scheduling.seed.enums;

import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SeededStaffUsers {
  STAFF1("staff1@flow.com", "Alice", "Johnson", LocalTime.of(9, 0),
      LocalTime.of(17, 0), 20, BigDecimal.valueOf(5000),
      BigDecimal.valueOf(1500), LocalDate.of(2023, 1, 1)),
  STAFF2("staff2@flow.com", "Bob", "Williams", LocalTime.of(10, 0),
      LocalTime.of(18, 0), 25, BigDecimal.valueOf(6000),
      BigDecimal.valueOf(1600), LocalDate.of(2023, 2, 1)),
  STAFF3("staff3@flow.com", "Charlie", "Brown", LocalTime.of(8, 0),
      LocalTime.of(16, 0), 30, BigDecimal.valueOf(7000),
      BigDecimal.valueOf(1700), LocalDate.of(2023, 3, 1)),
  STAFF4("staff4@flow.com", "David", "Smith", LocalTime.of(9, 0),
      LocalTime.of(17, 0), 35, BigDecimal.valueOf(8000),
      BigDecimal.valueOf(1800), LocalDate.of(2023, 4, 1)),
  STAFF5("staff5@flow.com", "Eve", "Davis", LocalTime.of(10, 0),
      LocalTime.of(18, 0), 40, BigDecimal.valueOf(9000),
      BigDecimal.valueOf(1900), LocalDate.of(2023, 5, 1)),
  STAFF6("staff6@flow.com", "Frank", "Miller", LocalTime.of(8, 0),
      LocalTime.of(16, 0), 45, BigDecimal.valueOf(10000),
      BigDecimal.valueOf(2000), LocalDate.of(2023, 6, 1)),
  STAFF7("staff7@flow.com", "Grace", "Wilson", LocalTime.of(9, 0),
      LocalTime.of(17, 0), 50, BigDecimal.valueOf(11000),
      BigDecimal.valueOf(2100), LocalDate.of(2023, 7, 1)),
  STAFF8("staff8@flow.com", "Hank", "Moore", LocalTime.of(10, 0),
      LocalTime.of(18, 0), 55, BigDecimal.valueOf(12000),
      BigDecimal.valueOf(2200), LocalDate.of(2023, 8, 1)),
  STAFF9("staff9@flow.com", "Ivy", "Taylor", LocalTime.of(8, 0),
      LocalTime.of(16, 0), 60, BigDecimal.valueOf(13000),
      BigDecimal.valueOf(2300), LocalDate.of(2023, 9, 1)),
  STAFF10("staff10@flow.com", "Jack", "Anderson", LocalTime.of(9, 0),
      LocalTime.of(17, 0), 65, BigDecimal.valueOf(14000),
      BigDecimal.valueOf(2400), LocalDate.of(2023, 10, 1));

  private final String email;
  private final String firstName;
  private final String lastName;
  private final LocalTime beginWorkingHour;
  private final LocalTime endWorkingHour;
  private final int completedAppointments;
  private final BigDecimal profit;
  private final BigDecimal salary;
  private final LocalDate startDate;

  private final static String PASSWORD = "$2a$10$v3jjdP2RNNpea0Lfb/GzP.Ujj1S4aSzDxXT/vWT2XobBTzexNZmAm";

  public User toUser() {
    User user = User.builder()
        .email(email)
        .password(PASSWORD)
        .firstName(firstName)
        .lastName(lastName)
        .role(UserRoles.EMPLOYEE)
        .services(new ArrayList<>())
        .build();

    StaffDetails staffDetails = StaffDetails.builder()
        .beginWorkingHour(beginWorkingHour)
        .endWorkingHour(endWorkingHour)
        .completedAppointments(completedAppointments)
        .profit(profit)
        .salary(salary)
        .isAvailable(true)
        .startDate(startDate)
        .build();
    staffDetails.setUser(user);
    user.setStaffDetails(staffDetails);

    return user;
  }
}