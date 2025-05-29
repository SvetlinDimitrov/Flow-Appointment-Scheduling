package com.internship.flow_appointment_scheduling.infrastructure.security.entity;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = "user")
public class RefreshToken {

  LocalDateTime expiryDate;

  @OneToOne
  User user;

  @Id
  @UuidGenerator
  private String id;

}
