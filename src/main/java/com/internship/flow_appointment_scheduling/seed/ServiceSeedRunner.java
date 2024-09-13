package com.internship.flow_appointment_scheduling.seed;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.work_space.entity.WorkSpace;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.work_space.repository.WorkSpaceRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

    initWorkSpaces().forEach(e -> {
      if (workSpaceRepository.findByName(e.getName()).isEmpty()) {
        workSpaceRepository.save(e);
      }
    });

    initServices(workSpaceRepository.findAll()).forEach(e -> {
      if (serviceRepository.findAllByName(e.getName()).isEmpty()) {
        serviceRepository.save(e);
      }
    });
  }


  private List<WorkSpace> initWorkSpaces() {
    Random random = new Random();

    WorkSpace workSpace1 = WorkSpace.builder()
        .name("Flow Main Building - First Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace2 = WorkSpace.builder()
        .name("Flow Main Building - Second Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace3 = WorkSpace.builder()
        .name("Flow Main Building - Third Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace4 = WorkSpace.builder()
        .name("Flow Annex - First Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace5 = WorkSpace.builder()
        .name("Flow Annex - Second Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace6 = WorkSpace.builder()
        .name("Flow West Wing - First Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace7 = WorkSpace.builder()
        .name("Flow West Wing - Second Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace8 = WorkSpace.builder()
        .name("Flow East Wing - First Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace9 = WorkSpace.builder()
        .name("Flow East Wing - Second Floor")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    WorkSpace workSpace10 = WorkSpace.builder()
        .name("Flow Conference Center")
        .availableSlots(random.nextInt(20) + 1)
        .build();

    return Arrays.asList(workSpace1, workSpace2, workSpace3, workSpace4, workSpace5, workSpace6,
        workSpace7, workSpace8, workSpace9, workSpace10);
  }

  private List<Service> initServices(List<WorkSpace> workSpaces) {
    Random random = new Random();

    Service service1 = Service.builder()
        .name("Yoga Training")
        .description("A relaxing yoga session to improve flexibility and reduce stress.")
        .duration(Duration.ofMinutes(60))
        .price(BigDecimal.valueOf(50.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service2 = Service.builder()
        .name("Deep Massage")
        .description("A deep tissue massage to relieve muscle tension and pain.")
        .duration(Duration.ofMinutes(90))
        .price(BigDecimal.valueOf(80.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service3 = Service.builder()
        .name("Personal Training")
        .description("One-on-one personal training session to achieve your fitness goals.")
        .duration(Duration.ofMinutes(60))
        .price(BigDecimal.valueOf(70.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service4 = Service.builder()
        .name("Pilates Class")
        .description("A pilates class to strengthen core muscles and improve posture.")
        .duration(Duration.ofMinutes(60))
        .price(BigDecimal.valueOf(55.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service5 = Service.builder()
        .name("Cardio Workout")
        .description("A high-intensity cardio workout to burn calories and improve endurance.")
        .duration(Duration.ofMinutes(45))
        .price(BigDecimal.valueOf(40.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service6 = Service.builder()
        .name("Strength Training")
        .description("A strength training session to build muscle and increase strength.")
        .duration(Duration.ofMinutes(60))
        .price(BigDecimal.valueOf(60.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service7 = Service.builder()
        .name("Zumba Class")
        .description("A fun and energetic Zumba class to improve fitness and coordination.")
        .duration(Duration.ofMinutes(60))
        .price(BigDecimal.valueOf(50.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service8 = Service.builder()
        .name("Meditation Session")
        .description("A guided meditation session to reduce stress and improve mental clarity.")
        .duration(Duration.ofMinutes(30))
        .price(BigDecimal.valueOf(30.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service9 = Service.builder()
        .name("Spin Class")
        .description("A high-energy spin class to improve cardiovascular fitness.")
        .duration(Duration.ofMinutes(45))
        .price(BigDecimal.valueOf(45.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    Service service10 = Service.builder()
        .name("Boxing Training")
        .description("A boxing training session to improve strength and agility.")
        .duration(Duration.ofMinutes(60))
        .price(BigDecimal.valueOf(65.00))
        .availability(true)
        .workSpace(workSpaces.get(random.nextInt(workSpaces.size())))
        .build();

    return Arrays.asList(service1, service2, service3, service4, service5, service6, service7,
        service8, service9, service10);
  }

}
