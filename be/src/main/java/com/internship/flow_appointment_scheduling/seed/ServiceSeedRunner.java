package com.internship.flow_appointment_scheduling.seed;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.work_space.repository.WorkSpaceRepository;
import com.internship.flow_appointment_scheduling.seed.enums.SeededServices;
import com.internship.flow_appointment_scheduling.seed.enums.SeededWorkSpaces;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile({"development", "test"})
@RequiredArgsConstructor
@Order(1)
public class ServiceSeedRunner implements ApplicationRunner {

  private final ServiceRepository serviceRepository;
  private final WorkSpaceRepository workSpaceRepository;

  @Override
  public void run(ApplicationArguments args){

    Arrays.stream(SeededWorkSpaces.values()).forEach(e -> {
      if (workSpaceRepository.findByName(e.getName()).isEmpty()) {
        workSpaceRepository.saveAndFlush(e.toWorkSpace());
      }
    });

    serviceWorkSpaceConnections().forEach((serviceEnum, workSpaceEnum) -> {
      Service service = serviceEnum.toService();
      service.setWorkSpace(
          workSpaceRepository.findByName(workSpaceEnum.getName())
              .orElseThrow(() -> new RuntimeException("WorkSpace not found"))
      );
      if (serviceRepository.findAllByName(service.getName()).isEmpty()) {
        serviceRepository.saveAndFlush(service);
      }
    });
  }

  private Map<SeededServices, SeededWorkSpaces> serviceWorkSpaceConnections() {
    Map<SeededServices, SeededWorkSpaces> serviceWorkSpaceMap = new HashMap<>();
    serviceWorkSpaceMap.put(SeededServices.YOGA_TRAINING, SeededWorkSpaces.WORKSPACE1);
    serviceWorkSpaceMap.put(SeededServices.DEEP_MASSAGE, SeededWorkSpaces.WORKSPACE2);
    serviceWorkSpaceMap.put(SeededServices.PERSONAL_TRAINING, SeededWorkSpaces.WORKSPACE3);
    serviceWorkSpaceMap.put(SeededServices.PILATES_CLASS, SeededWorkSpaces.WORKSPACE4);
    serviceWorkSpaceMap.put(SeededServices.CARDIO_WORKOUT, SeededWorkSpaces.WORKSPACE5);
    serviceWorkSpaceMap.put(SeededServices.STRENGTH_TRAINING, SeededWorkSpaces.WORKSPACE6);
    serviceWorkSpaceMap.put(SeededServices.ZUMBA_CLASS, SeededWorkSpaces.WORKSPACE7);
    serviceWorkSpaceMap.put(SeededServices.MEDITATION_SESSION, SeededWorkSpaces.WORKSPACE8);
    serviceWorkSpaceMap.put(SeededServices.SPIN_CLASS, SeededWorkSpaces.WORKSPACE9);
    serviceWorkSpaceMap.put(SeededServices.BOXING_TRAINING, SeededWorkSpaces.WORKSPACE10);
    return serviceWorkSpaceMap;
  }

}
