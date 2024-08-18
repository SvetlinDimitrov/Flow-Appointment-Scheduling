package com.internship.flow_appointment_scheduling.infrastructure.mappers;

import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeDetailsDto;
import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeDetailsView;
import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeModifyDto;
import com.internship.flow_appointment_scheduling.features.user.entity.EmployeeDetails;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", implementationName = "EmployeeDetailsMapperImpl")
public interface EmployeeDetailsMapper {

  EmployeeDetails toEntity(EmployeeDetailsDto employeeDetailsDto);

  EmployeeDetailsView toView(EmployeeDetails entity);

  void updateEntity(@MappingTarget EmployeeDetails entity, EmployeeModifyDto dto);
}
