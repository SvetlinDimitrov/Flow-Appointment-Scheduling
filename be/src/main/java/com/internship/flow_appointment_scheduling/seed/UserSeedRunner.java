package com.internship.flow_appointment_scheduling.seed;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.seed.enums.SeededAdminUsers;
import com.internship.flow_appointment_scheduling.seed.enums.SeededClientUsers;
import com.internship.flow_appointment_scheduling.seed.enums.SeededServices;
import com.internship.flow_appointment_scheduling.seed.enums.SeededStaffUsers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"development", "test"})
@RequiredArgsConstructor
@Order(2)
public class UserSeedRunner implements ApplicationRunner {

  public static String PASSWORD = "Password123!";
  private final UserRepository userRepository;
  private final ServiceRepository serviceRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    for (SeededAdminUsers admin : SeededAdminUsers.values()) {
      if (!userRepository.existsByEmail(admin.getEmail())) {
        var user = admin.toUser();
        user.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.saveAndFlush(user);
      }
    }

    for (SeededStaffUsers staff : SeededStaffUsers.values()) {
      if (!userRepository.existsByEmail(staff.getEmail())) {
        var user = staff.toUser();
        user.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.saveAndFlush(user);
      }
    }

    for (SeededClientUsers client : SeededClientUsers.values()) {
      if (!userRepository.existsByEmail(client.getEmail())) {
        var user = client.toUser();
        user.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.saveAndFlush(user);
      }
    }

    initUserServiceConnections();
  }

  private void initUserServiceConnections() {
    Map<SeededStaffUsers, List<SeededServices>> staffServiceMap = new HashMap<>();
    staffServiceMap.put(
        SeededStaffUsers.STAFF1,
        List.of(
            SeededServices.YOGA_TRAINING,
            SeededServices.PILATES_CLASS,
            SeededServices.MEDITATION_SESSION,
            SeededServices.CARDIO_WORKOUT));
    staffServiceMap.put(
        SeededStaffUsers.STAFF2,
        List.of(
            SeededServices.DEEP_MASSAGE,
            SeededServices.STRENGTH_TRAINING,
            SeededServices.BOXING_TRAINING,
            SeededServices.SPIN_CLASS));
    staffServiceMap.put(
        SeededStaffUsers.STAFF3,
        List.of(
            SeededServices.PERSONAL_TRAINING,
            SeededServices.CARDIO_WORKOUT,
            SeededServices.SPIN_CLASS,
            SeededServices.ZUMBA_CLASS));
    staffServiceMap.put(
        SeededStaffUsers.STAFF4,
        List.of(
            SeededServices.PILATES_CLASS,
            SeededServices.ZUMBA_CLASS,
            SeededServices.YOGA_TRAINING,
            SeededServices.MEDITATION_SESSION));
    staffServiceMap.put(
        SeededStaffUsers.STAFF5,
        List.of(
            SeededServices.CARDIO_WORKOUT,
            SeededServices.SPIN_CLASS,
            SeededServices.STRENGTH_TRAINING,
            SeededServices.BOXING_TRAINING));
    staffServiceMap.put(
        SeededStaffUsers.STAFF6,
        List.of(
            SeededServices.STRENGTH_TRAINING,
            SeededServices.BOXING_TRAINING,
            SeededServices.DEEP_MASSAGE,
            SeededServices.PERSONAL_TRAINING));
    staffServiceMap.put(
        SeededStaffUsers.STAFF7,
        List.of(
            SeededServices.ZUMBA_CLASS,
            SeededServices.YOGA_TRAINING,
            SeededServices.PILATES_CLASS,
            SeededServices.CARDIO_WORKOUT));
    staffServiceMap.put(
        SeededStaffUsers.STAFF8,
        List.of(
            SeededServices.MEDITATION_SESSION,
            SeededServices.PERSONAL_TRAINING,
            SeededServices.CARDIO_WORKOUT,
            SeededServices.SPIN_CLASS));
    staffServiceMap.put(
        SeededStaffUsers.STAFF9,
        List.of(
            SeededServices.SPIN_CLASS,
            SeededServices.STRENGTH_TRAINING,
            SeededServices.BOXING_TRAINING,
            SeededServices.DEEP_MASSAGE));
    staffServiceMap.put(
        SeededStaffUsers.STAFF10,
        List.of(
            SeededServices.BOXING_TRAINING,
            SeededServices.DEEP_MASSAGE,
            SeededServices.MEDITATION_SESSION,
            SeededServices.YOGA_TRAINING,
            SeededServices.PILATES_CLASS));

    staffServiceMap.forEach(
        (staff, services) -> {
          User staffUser = userRepository.findByEmail(staff.getEmail()).orElseThrow();
          if (staffUser.getServices().isEmpty()) {
            for (SeededServices service : services) {
              Service serviceEntity =
                  serviceRepository.findAllByName(service.getName()).stream()
                      .filter(s -> s.getDescription().equals(service.getDescription()))
                      .filter(s -> s.getDuration().equals(service.getDuration()))
                      .findFirst()
                      .orElseThrow(
                          () ->
                              new RuntimeException("Service not found with " + service.getName()));
              serviceEntity.getUsers().add(staffUser);
              serviceRepository.save(serviceEntity);
            }
          }
        });
  }
}
