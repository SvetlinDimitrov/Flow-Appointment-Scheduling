package com.internship.flow_appointment_scheduling.features.work_space.service;

import com.internship.flow_appointment_scheduling.features.work_space.entity.WorkSpace;
import java.util.List;

public interface WorkSpaceService {

  WorkSpace findByName(String name);

  List<String> getAllNames();
}
