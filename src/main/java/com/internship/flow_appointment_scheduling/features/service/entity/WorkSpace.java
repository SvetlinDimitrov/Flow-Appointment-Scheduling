package com.internship.flow_appointment_scheduling.features.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "work_spaces")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class WorkSpace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false , unique = true)
  private String name;

  @Column(nullable = false, name = "available_slots")
  private Integer availableSlots;

  @OneToMany(mappedBy = "workSpace")
  private List<Service> services;
}
