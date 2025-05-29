package com.internship.flow_appointment_scheduling.seed.enums;

import com.internship.flow_appointment_scheduling.features.work_space.entity.WorkSpace;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SeededWorkSpaces {
  WORKSPACE1("Flow Main Building - First Floor", 10),
  WORKSPACE2("Flow Main Building - Second Floor", 15),
  WORKSPACE3("Flow Main Building - Third Floor", 20),
  WORKSPACE4("Flow Annex - First Floor", 12),
  WORKSPACE5("Flow Annex - Second Floor", 18),
  WORKSPACE6("Flow West Wing - First Floor", 14),
  WORKSPACE7("Flow West Wing - Second Floor", 16),
  WORKSPACE8("Flow East Wing - First Floor", 11),
  WORKSPACE9("Flow East Wing - Second Floor", 17),
  WORKSPACE10("Flow Conference Center", 25);

  private final String name;
  private final int availableSlots;

  public WorkSpace toWorkSpace() {
    return WorkSpace.builder().name(name).availableSlots(availableSlots).build();
  }
}
