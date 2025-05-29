package com.internship.flow_appointment_scheduling.infrastructure.mappers.user;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffDetailsDto;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffDetailsView;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffModifyDto;
import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", implementationName = "StaffDetailsMapperImpl")
public interface StaffDetailsMapper {

  StaffDetails toEntity(StaffDetailsDto staffDetailsDto);

  @Mapping(
      source = "user.services",
      target = "serviceIds",
      qualifiedByName = "mapServicesToServiceIds")
  StaffDetailsView toView(StaffDetails entity);

  @Mapping(
      target = "salary",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(
      target = "user.role",
      source = "userRole",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(@MappingTarget StaffDetails entity, StaffModifyDto dto);

  @Named("mapServicesToServiceIds")
  default List<Long> mapServicesToServiceIds(List<Service> services) {
    return services.stream().map(Service::getId).collect(Collectors.toList());
  }
}
