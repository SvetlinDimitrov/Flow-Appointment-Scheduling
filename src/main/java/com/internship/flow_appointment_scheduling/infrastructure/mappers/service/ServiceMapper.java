package com.internship.flow_appointment_scheduling.infrastructure.mappers.service;

import com.internship.flow_appointment_scheduling.features.service.dto.service.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.service.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", implementationName = "ServiceMapperImpl")
public interface ServiceMapper {

  @Mapping(target = "workSpace.capacity", source = "workSpace.availableSlots")
  ServiceView toView(Service entity);

  Service toEntity(ServiceDTO dto);

  void updateEntity(@MappingTarget Service toUpdate, ServiceDTO dto);

}