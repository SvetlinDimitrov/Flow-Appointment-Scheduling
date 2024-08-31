package com.internship.flow_appointment_scheduling.infrastructure.mail_service;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Override
  public void sendAppointmentNotification(Appointment appointment) {
    String clientEmail = appointment.getClient().getEmail();
    String staffEmail = appointment.getStaff().getEmail();
    String subject = "Appointment Created Successfully";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Context context = new Context();
    context.setVariable("clientName", appointment.getClient().getFirstName());
    context.setVariable("startDate", appointment.getStartDate().format(formatter));
    context.setVariable("endDate", appointment.getEndDate().format(formatter));

    String htmlContent = templateEngine.process("appointment-notification", context);

    try {
      sendEmail(clientEmail, subject, htmlContent);
      sendEmail(staffEmail, subject, htmlContent);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void sendResetPasswordEmail(String jwtToken, String userEmail) {
    String subject = "Password Reset Request";
    String userName = "User";
    String resetLink = "http://localhost:3000/resetPassword?token=" + jwtToken;

    Context context = new Context();
    context.setVariable("userName", userName);
    context.setVariable("resetLink", resetLink);

    String htmlContent = templateEngine.process("password-reset", context);

    try {
      sendEmail(userEmail, subject, htmlContent);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }


  private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(htmlContent, true);
    mailSender.send(message);
  }
}