package com.internship.flow_appointment_scheduling.features.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "employee_details")
@Getter
@Setter
@ToString
public class EmployeeDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private BigDecimal salary;

  @Column(nullable = false)
  private BigDecimal profit;

  @Column(name = "completed_appointments", nullable = false)
  private Integer completedAppointments;

  @Column(nullable = false)
  private Double experience;

  @Column(name = "begin_working_hour", nullable = false)
  private LocalTime beginWorkingHour;

  @Column(name = "end_working_hour", nullable = false)
  private LocalTime endWorkingHour;

  @OneToOne
  private User user;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmployeeDetails that = (EmployeeDetails) o;
    return Objects.equals(id, that.id) && Objects.equals(salary, that.salary)
        && Objects.equals(profit, that.profit) && Objects.equals(
        completedAppointments, that.completedAppointments) && Objects.equals(experience,
        that.experience) && Objects.equals(beginWorkingHour, that.beginWorkingHour)
        && Objects.equals(endWorkingHour, that.endWorkingHour) && Objects.equals(
        user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, salary, profit, completedAppointments, experience, beginWorkingHour,
        endWorkingHour, user);
  }
}
