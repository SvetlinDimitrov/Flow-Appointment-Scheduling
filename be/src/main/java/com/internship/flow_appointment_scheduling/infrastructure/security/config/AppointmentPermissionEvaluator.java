package com.internship.flow_appointment_scheduling.infrastructure.security.config;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component(value = "appointmentPermissionEvaluator")
@RequiredArgsConstructor
public class AppointmentPermissionEvaluator {

  private final AppointmentRepository appointmentRepository;

  public boolean currentClientOrStaffAccess(Authentication authentication, Long appointmentId) {
    if (authentication != null
        && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

      return appointmentRepository.findById(appointmentId)
          .filter(value ->
              userDetails.user().getEmail().equals(value.getClient().getEmail()) ||
                  userDetails.user().getEmail().equals(value.getStaff().getEmail())
          ).isPresent();

    }
    return false;
  }

  public boolean currentClientOrStaffAccess(Authentication authentication, AppointmentCreate appointmentCreate) {
    if (authentication != null
        && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

      String userEmail = userDetails.user().getEmail();
      return userEmail.equals(appointmentCreate.clientEmail()) ||
          userEmail.equals(appointmentCreate.staffEmail());
    }
    return false;
  }

}
