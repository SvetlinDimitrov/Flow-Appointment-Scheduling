package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;


public class UserAuth extends User {

  private final Long id;

  public UserAuth(com.internship.flow_appointment_scheduling.features.user.entity.User user) {
    super(user.getEmail(),
        user.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
    );

    this.id = user.getId();
  }

  public Long getId() {
    return id;
  }

}
