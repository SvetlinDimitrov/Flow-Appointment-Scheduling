package com.internship.flow_appointment_scheduling.features.work_space.entity;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "work_spaces")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "services")
@EqualsAndHashCode(exclude = "services")
public class WorkSpace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false , unique = true)
  private String name;

  @Column(nullable = false, name = "available_slots")
  private Integer availableSlots;

  @OneToMany(mappedBy = "workSpace")
  @Builder.Default
  private List<Service> services = new ArrayList<>();
}
