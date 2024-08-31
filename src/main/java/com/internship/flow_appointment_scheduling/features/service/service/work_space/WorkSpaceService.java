package com.internship.flow_appointment_scheduling.features.service.service.work_space;

import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;
import java.util.List;

public interface WorkSpaceService {

  WorkSpace findByName(String name);

  List<String> getAllNames();
}
