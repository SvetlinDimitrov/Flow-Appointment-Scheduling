package com.internship.flow_appointment_scheduling.infrastructure.mappers.appointment;

import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentCreate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentUpdate;
import com.internship.flow_appointment_scheduling.features.appointments.dto.AppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.ShortAppointmentView;
import com.internship.flow_appointment_scheduling.features.appointments.dto.enums.UpdateAppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.service.ServiceMapper;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, ServiceMapper.class},
    implementationName = "AppointmentMapperImpl")
public interface AppointmentMapper {

  AppointmentView toView(Appointment entity);

  @Mapping(target = "serviceName", source = "service.name")
  ShortAppointmentView toViewShort(Appointment entity);

  @Mapping(target = "startDate", source = "date")
  Appointment toEntity(AppointmentCreate dto);

  @Mapping(
      target = "status",
      source = "status",
      nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(@MappingTarget Appointment appointment, AppointmentUpdate dto);

  default AppointmentStatus map(UpdateAppointmentStatus status) {
    if (status == null) {
      return null;
    }
    return AppointmentStatus.valueOf(status.name());
  }
}
