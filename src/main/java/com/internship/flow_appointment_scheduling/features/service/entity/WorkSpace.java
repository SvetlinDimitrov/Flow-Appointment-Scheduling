package com.internship.flow_appointment_scheduling.features.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "work_spaces")
public class WorkSpace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false , unique = true)
  private String name;

  @Column(name = "available_slots_at_a_time")
  private Integer capacity;

  @OneToMany(mappedBy = "workSpace")
  private List<Service> services;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
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
    WorkSpace workSpace = (WorkSpace) o;
    return Objects.equals(id, workSpace.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "WorkSpace{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", capacity=" + capacity +
        ", services=" + services +
        '}';
  }
}
