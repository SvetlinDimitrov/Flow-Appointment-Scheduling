package com.internship.flow_appointment_scheduling.features.service.dto.work_space;

import com.internship.flow_appointment_scheduling.features.service.annotations.unique_work_space_name.UniqueWorkSpaceName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WorkSpaceDTO(

    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    @UniqueWorkSpaceName
    String name,

    @NotNull(message = "Capacity is mandatory")
    @Min(value = 1, message = "Capacity must be greater than 0")
    Integer capacity) {
}
