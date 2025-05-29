package com.internship.flow_appointment_scheduling.infrastructure.events.appointments;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppointmentNotificationEvent extends ApplicationEvent {

    public enum NotificationType {
        APPROVED, NOT_APPROVED, CANCELED
    }

    private final Appointment appointment;
    private final NotificationType notificationType;

    public AppointmentNotificationEvent(Object source, Appointment appointment, NotificationType notificationType) {
        super(source);
        this.appointment = appointment;
        this.notificationType = notificationType;
    }
}