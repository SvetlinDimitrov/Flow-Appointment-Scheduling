package com.internship.flow_appointment_scheduling.features.user.annotations.working_hours;

import java.time.LocalTime;

public interface WorkingHours {
    LocalTime getBeginWorkingHour();
    LocalTime getEndWorkingHour();
}