package com.internship.flow_appointment_scheduling.infrastructure.mail_service;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;

public interface MailService {

  void sendAppointmentNotification(Appointment appointment);

  void sendResetPasswordEmail(String jwtToken , String userEmail);
}
