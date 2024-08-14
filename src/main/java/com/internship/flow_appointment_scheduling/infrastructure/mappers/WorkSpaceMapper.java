package com.internship.flow_appointment_scheduling.infrastructure.mappers;

import com.internship.flow_appointment_scheduling.features.service.dto.WorkSpacePostPutRequest;
import com.internship.flow_appointment_scheduling.features.service.dto.WorkSpaceView;
import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", implementationName = "WorkSpaceMapperImpl")
public interface WorkSpaceMapper {

  WorkSpaceView toView(WorkSpace entity);

  WorkSpace toEntity(WorkSpacePostPutRequest dto);

  void updateEntity(@MappingTarget WorkSpace toUpdate, WorkSpacePostPutRequest dto);
}
