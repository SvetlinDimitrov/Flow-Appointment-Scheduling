package com.internship.flow_appointment_scheduling.seed.enums;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import java.util.ArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SeededClientUsers {
  CLIENT1("client1@abv.bg", "Alice", "Smith"),
  CLIENT2("client2@abv.bg", "Bob", "Johnson"),
  CLIENT3("client3@abv.bg", "Charlie", "Williams"),
  CLIENT4("client4@abv.bg", "David", "Brown"),
  CLIENT5("client5@abv.bg", "Eve", "Jones"),
  CLIENT6("client6@abv.bg", "Frank", "Miller"),
  CLIENT7("client7@abv.bg", "Grace", "Davis"),
  CLIENT8("client8@abv.bg", "Hank", "Garcia"),
  CLIENT9("client9@abv.bg", "Ivy", "Rodriguez"),
  CLIENT10("client10@abv.bg", "Jack", "Wilson"),
  CLIENT11("client11@abv.bg", "Kathy", "Martinez"),
  CLIENT12("client12@abv.bg", "Leo", "Anderson"),
  CLIENT13("client13@abv.bg", "Mona", "Taylor"),
  CLIENT14("client14@abv.bg", "Nina", "Thomas"),
  CLIENT15("client15@abv.bg", "Oscar", "Hernandez"),
  CLIENT16("client16@abv.bg", "Paul", "Moore"),
  CLIENT17("client17@abv.bg", "Quincy", "Martin"),
  CLIENT18("client18@abv.bg", "Rita", "Jackson"),
  CLIENT19("client19@abv.bg", "Steve", "Thompson"),
  CLIENT20("client20@abv.bg", "Tina", "White");

  private static final String PASSWORD =
      "$2a$10$v3jjdP2RNNpea0Lfb/GzP.Ujj1S4aSzDxXT/vWT2XobBTzexNZmAm";
  private final String email;
  private final String firstName;
  private final String lastName;

  public User toUser() {
    return User.builder()
        .email(email)
        .password(PASSWORD)
        .firstName(firstName)
        .lastName(lastName)
        .role(UserRoles.CLIENT)
        .services(new ArrayList<>())
        .build();
  }
}