package com.internship.flow_appointment_scheduling.features.appointment.entity;

import com.internship.flow_appointment_scheduling.features.appointment.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@EqualsAndHashCode(exclude = {"client", "staff", "service"})
@ToString(exclude = {"client", "staff", "service"})
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
  private User client;

  @ManyToOne
  @JoinColumn(name = "staff_id", nullable = false)
  private User staff;

  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private AppointmentStatus status;

  @ManyToOne
  @JoinColumn(name = "service_id", nullable = false)
  private Service service;
}