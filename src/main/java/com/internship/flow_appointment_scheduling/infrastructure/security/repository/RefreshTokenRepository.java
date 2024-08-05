package com.internship.flow_appointment_scheduling.infrastructure.security.repository;

import com.internship.flow_appointment_scheduling.infrastructure.security.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
