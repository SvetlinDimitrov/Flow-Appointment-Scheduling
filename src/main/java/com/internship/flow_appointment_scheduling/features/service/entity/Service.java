package com.internship.flow_appointment_scheduling.features.service.entity;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "services")
public class Service {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false , name = "duration_minutes")
  private Integer duration;

  @Column(nullable = false)
  private BigDecimal price;

  @ManyToOne
  private WorkSpace workSpace;

  @ManyToMany
  @JoinTable(
      name = "users_services",
      joinColumns = @JoinColumn(name = "service_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private List<User> users;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public WorkSpace getWorkSpace() {
    return workSpace;
  }

  public void setWorkSpace(
      WorkSpace workSpace) {
    this.workSpace = workSpace;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(
      List<User> users) {
    this.users = users;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Service service = (Service) o;
    return Objects.equals(id, service.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Service{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", duration=" + duration +
        ", price=" + price +
        ", workSpace=" + workSpace +
        ", users=" + users +
        '}';
  }
}
