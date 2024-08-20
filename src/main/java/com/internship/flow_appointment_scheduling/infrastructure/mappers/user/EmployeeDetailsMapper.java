package com.internship.flow_appointment_scheduling.infrastructure.mappers.user;

import com.internship.flow_appointment_scheduling.features.user.dto.employee_details.EmployeeDetailsDto;
import com.internship.flow_appointment_scheduling.features.user.dto.employee_details.EmployeeDetailsView;
import com.internship.flow_appointment_scheduling.features.user.dto.employee_details.EmployeeModifyDto;
import com.internship.flow_appointment_scheduling.features.user.entity.EmployeeDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", implementationName = "EmployeeDetailsMapperImpl")
public interface EmployeeDetailsMapper {

  EmployeeDetails toEntity(EmployeeDetailsDto employeeDetailsDto);

  EmployeeDetailsView toView(EmployeeDetails entity);

  @Mapping(target = "user.role", source = "userRole")
  void updateEntity(@MappingTarget EmployeeDetails entity, EmployeeModifyDto dto);
}
