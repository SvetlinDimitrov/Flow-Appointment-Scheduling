package com.internship.flow_appointment_scheduling.infrastructure.security.entity;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

  @Id
  @UuidGenerator
  private String id;

  LocalDateTime expiryDate;

  @OneToOne
  User user;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDateTime expiryDate) {
    this.expiryDate = expiryDate;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RefreshToken that = (RefreshToken) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "RefreshToken{" +
        "id=" + id +
        ", expirationTime=" + expiryDate +
        ", user=" + user +
        '}';
  }
}
