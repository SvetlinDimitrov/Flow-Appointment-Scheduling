package com.internship.flow_appointment_scheduling.infrastructure.security.repository;

import com.internship.flow_appointment_scheduling.features.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
}
