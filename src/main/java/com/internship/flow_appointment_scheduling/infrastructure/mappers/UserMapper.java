package com.internship.flow_appointment_scheduling.infrastructure.mappers;

import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", implementationName = "UserMapperImpl")
public interface UserMapper {

  UserView toView(User entity);

  User toEntity(UserPostRequest dto);

  void updateEntity(@MappingTarget User toUpdate, UserPutRequest dto);

}
