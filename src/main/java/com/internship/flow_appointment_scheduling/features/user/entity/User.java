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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
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

  @OneToOne(mappedBy = "user", cascade = {CascadeType.REMOVE , CascadeType.PERSIST})
  private EmployeeDetails employeeDetails;

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
}
