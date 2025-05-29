package com.internship.flow_appointment_scheduling.infrastructure.scheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Getter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
@Getter
public class SchedulerService {

  private final TaskScheduler taskScheduler;

  public SchedulerService() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    scheduler.initialize();
    this.taskScheduler = scheduler;
  }

  public void scheduleTask(Runnable task, LocalDateTime dateTime) {
    Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
    taskScheduler.schedule(task, instant);
  }
}
