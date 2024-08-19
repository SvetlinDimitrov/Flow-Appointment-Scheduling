package com.internship.flow_appointment_scheduling.features.service.service.work_space;

import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;
import com.internship.flow_appointment_scheduling.features.service.repository.WorkSpaceRepository;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import org.springframework.stereotype.Service;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

  private final WorkSpaceRepository workSpaceRepository;

  public WorkSpaceServiceImpl(WorkSpaceRepository workSpaceRepository) {
    this.workSpaceRepository = workSpaceRepository;
  }

  @Override
  public WorkSpace findByName(String name) {
    return workSpaceRepository.findByName(name)
        .orElseThrow(() -> new NotFoundException(
            Exceptions.WORK_SPACE_NOT_FOUND, name)
        );
  }
}
