package com.internship.flow_appointment_scheduling.features.service.service.work_space;

import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;

public interface WorkSpaceService {

  WorkSpace findByName(String name);
}
