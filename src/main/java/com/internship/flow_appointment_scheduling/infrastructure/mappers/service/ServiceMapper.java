package com.internship.flow_appointment_scheduling.infrastructure.mappers.service;

import com.internship.flow_appointment_scheduling.features.service.dto.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import java.time.Duration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", implementationName = "ServiceMapperImpl")
public interface ServiceMapper {

  @Mapping(target = "workSpace.capacity", source = "workSpace.availableSlots")
  @Mapping(target = "durationInMinutes", source = "duration", qualifiedByName = "durationToMinutes")
  ServiceView toView(Service entity);

  Service toEntity(ServiceDTO dto);

  @Mapping(target = "duration", source = "durationInMinutes", qualifiedByName = "minutesToDuration")
  void updateEntity(@MappingTarget Service toUpdate, ServiceDTO dto);

  @Named("durationToMinutes")
  default Long durationToMinutes(Duration duration) {
    return duration == null ? null : duration.toMinutes();
  }

  @Named("minutesToDuration")
  default Duration minutesToDuration(Long minutes) {
    return minutes == null ? null : Duration.ofMinutes(minutes);
  }
}