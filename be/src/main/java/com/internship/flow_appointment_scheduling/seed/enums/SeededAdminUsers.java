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
public enum SeededAdminUsers {
  ADMIN1("admin1@flow.com", "George", "Smith", LocalTime.of(10, 0),
      LocalTime.of(18, 0), 46, BigDecimal.valueOf(15000),
      BigDecimal.valueOf(2000), LocalDate.of(2023, 11, 1)),
  ADMIN2("admin2@flow.com", "John", "Doe", LocalTime.of(9, 0),
      LocalTime.of(17, 0), 30, BigDecimal.valueOf(10000),
      BigDecimal.valueOf(1800), LocalDate.of(2023, 10, 1)),
  ADMIN3("admin3@flow.com", "Jane", "Doe", LocalTime.of(8, 0),
      LocalTime.of(16, 0), 25, BigDecimal.valueOf(12000),
      BigDecimal.valueOf(1900), LocalDate.of(2023, 9, 1));

  private static final String PASSWORD =
      "$2a$10$v3jjdP2RNNpea0Lfb/GzP.Ujj1S4aSzDxXT/vWT2XobBTzexNZmAm";
  private final String email;
  private final String firstName;
  private final String lastName;
  private final LocalTime beginWorkingHour;
  private final LocalTime endWorkingHour;
  private final int completedAppointments;
  private final BigDecimal profit;
  private final BigDecimal salary;
  private final LocalDate startDate;

  public User toUser() {
    User user = User.builder()
        .email(email)
        .password(PASSWORD)
        .firstName(firstName)
        .lastName(lastName)
        .role(UserRoles.ADMINISTRATOR)
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