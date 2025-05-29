package com.internship.flow_appointment_scheduling.infrastructure.mail_service;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;

public interface MailService {

  void sendApprovedAppointmentNotification(Appointment appointment);

  void sendNotApprovedAppointmentNotification(Appointment appointment);

  void sendCanceledAppointmentNotificationToClient(Appointment appointment);

  void sendResetPasswordEmail(String jwtToken, String userEmail);
}
