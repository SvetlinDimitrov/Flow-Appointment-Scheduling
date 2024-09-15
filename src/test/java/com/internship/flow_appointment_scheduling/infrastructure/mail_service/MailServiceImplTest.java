package com.internship.flow_appointment_scheduling.infrastructure.mail_service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.ServiceUnavailableException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private TemplateEngine templateEngine;

  @InjectMocks
  private MailServiceImpl mailService;

  @BeforeEach
  void setUp() {
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
  }

  @Test
  void sendApprovedAppointmentNotification_sendsEmail_whenValidAppointment() {
    Appointment appointment = createMockAppointment();
    when(templateEngine.process(anyString(), any(Context.class)))
        .thenReturn("emailContent");

    mailService.sendApprovedAppointmentNotification(appointment);

    verify(mailSender, times(2))
        .send(any(MimeMessage.class));
  }

  @Test
  void sendApprovedAppointmentNotification_logsError_whenMailAuthenticationException() {
    Appointment appointment = createMockAppointment();
    Throwable cause = mock(Throwable.class);
    MailAuthenticationException exception = new MailAuthenticationException(
        "Authentication failed");
    exception.initCause(cause);

    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("emailContent");
    when(cause.getMessage()).thenReturn("Cause message");
    doThrow(exception).when(mailSender).send(any(MimeMessage.class));

    mailService.sendApprovedAppointmentNotification(appointment);

    verify(mailSender, times(1)).send(any(MimeMessage.class));
  }

  @Test
  void sendApprovedAppointmentNotification_throwsException_whenMessagingException() {
    Appointment appointment = createMockAppointment();
    Exception cause = new Exception("Cause message");
    MessagingException exception = new MessagingException("Messaging failed", cause);

    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("emailContent");

    doAnswer(invocation -> {
      throw exception;
    }).when(mailSender).send(any(MimeMessage.class));

    assertThrows(ServiceUnavailableException.class, () -> {
      mailService.sendApprovedAppointmentNotification(appointment);
    });
  }

  @Test
  void sendNotApprovedAppointmentNotification_sendsEmail_whenValidAppointment() {
    Appointment appointment = createMockAppointment();
    when(templateEngine.process(anyString(), any(Context.class)))
        .thenReturn("emailContent");

    mailService.sendNotApprovedAppointmentNotification(appointment);

    verify(mailSender, times(2))
        .send(any(MimeMessage.class));
  }

  @Test
  void sendNotApprovedAppointmentNotification_logsError_whenMailAuthenticationException() {
    Appointment appointment = createMockAppointment();
    Throwable cause = mock(Throwable.class);
    MailAuthenticationException exception = new MailAuthenticationException(
        "Authentication failed");
    exception.initCause(cause);

    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("emailContent");
    when(cause.getMessage()).thenReturn("Cause message");
    doThrow(exception).when(mailSender).send(any(MimeMessage.class));

    mailService.sendNotApprovedAppointmentNotification(appointment);

    verify(mailSender, times(1)).send(any(MimeMessage.class));
  }

  @Test
  void sendNotApprovedAppointmentNotification_throwsException_whenMessagingException() {
    Appointment appointment = createMockAppointment();
    Exception cause = new Exception("Cause message");
    MessagingException exception = new MessagingException("Messaging failed", cause);

    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("emailContent");

    doAnswer(invocation -> {
      throw exception;
    }).when(mailSender).send(any(MimeMessage.class));

    assertThrows(ServiceUnavailableException.class, () -> {
      mailService.sendNotApprovedAppointmentNotification(appointment);
    });
  }

  @Test
  void sendCanceledAppointmentNotificationToClient_sendsEmail_whenValidAppointment() {
    Appointment appointment = mock(Appointment.class);
    User client = mock(User.class);
    Service service = mock(Service.class);

    when(client.getEmail()).thenReturn("client@example.com");
    when(service.getName()).thenReturn("ServiceName");
    when(appointment.getClient()).thenReturn(client);
    when(appointment.getService()).thenReturn(service);
    when(appointment.getStartDate()).thenReturn(LocalDateTime.now());
    when(appointment.getEndDate()).thenReturn(LocalDateTime.now().plusHours(1));
    when(templateEngine.process(anyString(), any(Context.class)))
        .thenReturn("emailContent");

    mailService.sendCanceledAppointmentNotificationToClient(appointment);

    verify(mailSender, times(1))
        .send(any(MimeMessage.class));
  }

  @Test
  void sendCanceledAppointmentNotificationToClient_logsError_whenMailAuthenticationException() {
    Appointment appointment = mock(Appointment.class);
    User client = mock(User.class);
    Service service = mock(Service.class);
    Throwable cause = mock(Throwable.class);
    MailAuthenticationException exception = new MailAuthenticationException(
        "Authentication failed");
    exception.initCause(cause);

    when(client.getEmail()).thenReturn("client@example.com");
    when(service.getName()).thenReturn("ServiceName");
    when(appointment.getClient()).thenReturn(client);
    when(appointment.getService()).thenReturn(service);
    when(appointment.getStartDate()).thenReturn(LocalDateTime.now());
    when(appointment.getEndDate()).thenReturn(LocalDateTime.now().plusHours(1));
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("emailContent");
    when(cause.getMessage()).thenReturn("Cause message");
    doThrow(exception).when(mailSender).send(any(MimeMessage.class));

    mailService.sendCanceledAppointmentNotificationToClient(appointment);

    verify(mailSender, times(1)).send(any(MimeMessage.class));
  }

  @Test
  void sendCanceledAppointmentNotificationToClient_throwsException_whenMessagingException() {
    Appointment appointment = mock(Appointment.class);
    User client = mock(User.class);
    Service service = mock(Service.class);
    Exception cause = new Exception("Cause message");
    MessagingException exception = new MessagingException("Messaging failed", cause);

    when(client.getEmail()).thenReturn("client@example.com");
    when(service.getName()).thenReturn("ServiceName");
    when(appointment.getClient()).thenReturn(client);
    when(appointment.getService()).thenReturn(service);
    when(appointment.getStartDate()).thenReturn(LocalDateTime.now());
    when(appointment.getEndDate()).thenReturn(LocalDateTime.now().plusHours(1));
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("emailContent");

    doAnswer(invocation -> {
      throw exception;
    }).when(mailSender).send(any(MimeMessage.class));

    assertThrows(ServiceUnavailableException.class, () -> {
      mailService.sendCanceledAppointmentNotificationToClient(appointment);
    });
  }

  @Test
  void sendResetPasswordEmail_sendsEmail_whenValidRequest() {
    String jwtToken = "validToken";
    String userEmail = "user@example.com";
    when(templateEngine.process(anyString(), any(Context.class)))
        .thenReturn("emailContent");

    mailService.sendResetPasswordEmail(jwtToken, userEmail);

    verify(mailSender, times(1))
        .send(any(MimeMessage.class));
  }

  @Test
  void sendResetPasswordEmail_logsError_whenMailAuthenticationException() {
    String jwtToken = "validToken";
    String userEmail = "user@example.com";
    Throwable cause = mock(Throwable.class);
    MailAuthenticationException exception = new MailAuthenticationException(
        "Authentication failed");
    exception.initCause(cause);

    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("emailContent");
    when(cause.getMessage()).thenReturn("Cause message");
    doThrow(exception).when(mailSender).send(any(MimeMessage.class));

    mailService.sendResetPasswordEmail(jwtToken, userEmail);

    verify(mailSender, times(1)).send(any(MimeMessage.class));
  }

  @Test
  void sendResetPasswordEmail_throwsException_whenMessagingException() {
    String jwtToken = "validToken";
    String userEmail = "user@example.com";
    Exception cause = new Exception("Cause message");
    MessagingException exception = new MessagingException("Messaging failed", cause);

    when(templateEngine.process(anyString(), any(Context.class))).thenReturn("emailContent");

    doAnswer(invocation -> {
      throw exception;
    }).when(mailSender).send(any(MimeMessage.class));

    assertThrows(ServiceUnavailableException.class, () -> {
      mailService.sendResetPasswordEmail(jwtToken, userEmail);
    });
  }

  private Appointment createMockAppointment() {
    Appointment appointment = mock(Appointment.class);
    User client = mock(User.class);
    User staff = mock(User.class);
    Service service = mock(Service.class);

    when(client.getEmail()).thenReturn("client@example.com");
    when(staff.getEmail()).thenReturn("staff@example.com");
    when(staff.getFirstName()).thenReturn("StaffName");
    when(service.getName()).thenReturn("ServiceName");
    when(appointment.getClient()).thenReturn(client);
    when(appointment.getStaff()).thenReturn(staff);
    when(appointment.getService()).thenReturn(service);
    when(appointment.getStartDate()).thenReturn(LocalDateTime.now());
    when(appointment.getEndDate()).thenReturn(LocalDateTime.now().plusHours(1));

    return appointment;
  }
}