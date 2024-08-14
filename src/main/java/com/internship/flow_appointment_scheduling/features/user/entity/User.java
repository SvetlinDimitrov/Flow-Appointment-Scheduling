package com.internship.flow_appointment_scheduling.features.user.entity;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.infrastructure.security.entity.RefreshToken;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "varchar(255)")
  private String firstName;

  @Column(columnDefinition = "varchar(255)")
  private String lastName;

  @Column(columnDefinition = "varchar(255)", unique = true, nullable = false)
  private String email;

  @Column(columnDefinition = "varchar(255)", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRoles role;

  @OneToOne(mappedBy = "user", cascade = {CascadeType.REMOVE})
  private RefreshToken refreshToken;

  @ManyToMany(mappedBy = "users")
  private List<Service> services;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRoles getRole() {
    return role;
  }

  public void setRole(UserRoles role) {
    this.role = role;
  }

  public RefreshToken getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(RefreshToken refreshToken) {
    this.refreshToken = refreshToken;
  }

  public List<Service> getServices() {
    return services;
  }

  public void setServices(
      List<Service> services) {
    this.services = services;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", email='" + email + '\'' +
        ", password='" + password + '\'' +
        ", role=" + role +
        '}';
  }
}
