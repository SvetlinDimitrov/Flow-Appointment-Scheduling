package com.internship.flow_appointment_scheduling.seed.enums;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import java.math.BigDecimal;
import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SeededServices {
  YOGA_TRAINING(
      "Yoga Training",
      "A relaxing yoga session to improve flexibility and reduce stress.",
      Duration.ofMinutes(60),
      BigDecimal.valueOf(50.00)),
  DEEP_MASSAGE(
      "Deep Massage",
      "A deep tissue massage to relieve muscle tension and pain.",
      Duration.ofMinutes(90),
      BigDecimal.valueOf(80.00)),
  PERSONAL_TRAINING(
      "Personal Training",
      "One-on-one personal training session to achieve your fitness goals.",
      Duration.ofMinutes(60),
      BigDecimal.valueOf(70.00)),
  PILATES_CLASS(
      "Pilates Class",
      "A pilates class to strengthen core muscles and improve posture.",
      Duration.ofMinutes(60),
      BigDecimal.valueOf(55.00)),
  CARDIO_WORKOUT(
      "Cardio Workout",
      "A high-intensity cardio workout to burn calories and improve endurance.",
      Duration.ofMinutes(45),
      BigDecimal.valueOf(40.00)),
  STRENGTH_TRAINING(
      "Strength Training",
      "A strength training session to build muscle and increase strength.",
      Duration.ofMinutes(60),
      BigDecimal.valueOf(60.00)),
  ZUMBA_CLASS(
      "Zumba Class",
      "A fun and energetic Zumba class to improve fitness and coordination.",
      Duration.ofMinutes(60),
      BigDecimal.valueOf(50.00)),
  MEDITATION_SESSION(
      "Meditation Session",
      "A guided meditation session to reduce stress and improve mental clarity.",
      Duration.ofMinutes(30),
      BigDecimal.valueOf(30.00)),
  SPIN_CLASS(
      "Spin Class",
      "A high-energy spin class to improve cardiovascular fitness.",
      Duration.ofMinutes(45),
      BigDecimal.valueOf(45.00)),
  BOXING_TRAINING(
      "Boxing Training",
      "A boxing training session to improve strength and agility.",
      Duration.ofMinutes(60),
      BigDecimal.valueOf(65.00));

  private final String name;
  private final String description;
  private final Duration duration;
  private final BigDecimal price;

  public Service toService() {
    return Service.builder()
        .name(name)
        .description(description)
        .duration(duration)
        .price(price)
        .availability(true)
        .build();
  }
}
