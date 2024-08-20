package com.internship.flow_appointment_scheduling.infrastructure.mappers.service;

import com.internship.flow_appointment_scheduling.features.service.dto.WorkSpaceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.WorkSpaceView;
import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", implementationName = "WorkSpaceMapperImpl")
public interface WorkSpaceMapper {

  WorkSpaceView toView(WorkSpace entity);

  WorkSpace toEntity(WorkSpaceDTO dto);

  void updateEntity(@MappingTarget WorkSpace toUpdate, WorkSpaceDTO dto);
}
