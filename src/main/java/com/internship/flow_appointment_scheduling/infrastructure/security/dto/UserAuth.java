package com.internship.flow_appointment_scheduling.infrastructure.security.dto;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class UserAuth implements UserDetails {

  private final User entity;

  public UserAuth(User entity) {
    this.entity = entity;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + entity.getRole().name()));
  }

  @Override
  public String getPassword() {
    return entity.getPassword();
  }

  @Override
  public String getUsername() {
    return entity.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }

  public User getEntity() {
    return entity;
  }
}
