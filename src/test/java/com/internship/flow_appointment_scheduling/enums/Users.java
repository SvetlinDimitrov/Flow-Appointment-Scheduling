package com.internship.flow_appointment_scheduling.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Users {

  ADMIN("admin1@flow.com"), STAFF("staff1@flow.com"), CLIENT("client1@abv.bg");

  private final String email;
}
