package com.intership.flow_appointment_scheduling.infrastructure.mappers;

import com.intership.flow_appointment_scheduling.feature.user.dto.UserPostRequest;
import com.intership.flow_appointment_scheduling.feature.user.dto.UserPutRequest;
import com.intership.flow_appointment_scheduling.feature.user.dto.UserView;
import com.intership.flow_appointment_scheduling.feature.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", implementationName = "UserMapperImpl")
public interface UserMapper {

  UserView toUserView(User entity);

  User toUserEntity(UserPostRequest dto);

  User toUserEntity(UserPutRequest dto);
}
