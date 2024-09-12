package com.internship.flow_appointment_scheduling.infrastructure.mappers.service;

import com.internship.flow_appointment_scheduling.features.work_space.dto.WorkSpaceDTO;
import com.internship.flow_appointment_scheduling.features.work_space.dto.WorkSpaceView;
import com.internship.flow_appointment_scheduling.features.work_space.entity.WorkSpace;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", implementationName = "WorkSpaceMapperImpl")
public interface WorkSpaceMapper {

  WorkSpaceView toView(WorkSpace entity);

  WorkSpace toEntity(WorkSpaceDTO dto);

  void updateEntity(@MappingTarget WorkSpace toUpdate, WorkSpaceDTO dto);
}
