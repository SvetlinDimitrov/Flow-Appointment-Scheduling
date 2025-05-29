package com.internship.flow_appointment_scheduling.features.user.entity;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"services", "refreshToken", "staffDetails"})
@EqualsAndHashCode(exclude = {"services", "refreshToken", "staffDetails"})
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

  @Column(columnDefinition = "varchar(255)", name = "password_reset_token")
  private String passwordResetToken;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private UserRoles role = UserRoles.CLIENT;

  @OneToOne(
      mappedBy = "user",
      cascade = {CascadeType.REMOVE})
  private RefreshToken refreshToken;

  @ManyToMany(mappedBy = "users")
  @Builder.Default
  private List<Service> services = new ArrayList<>();

  @OneToOne(
      mappedBy = "user",
      cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
  private StaffDetails staffDetails;

  @OneToMany(
      mappedBy = "client",
      cascade = {CascadeType.REMOVE})
  @Builder.Default
  private List<Appointment> clientAppointments = new ArrayList<>();

  @OneToMany(
      mappedBy = "staff",
      cascade = {CascadeType.REMOVE})
  @Builder.Default
  private List<Appointment> staffAppointments = new ArrayList<>();
}
