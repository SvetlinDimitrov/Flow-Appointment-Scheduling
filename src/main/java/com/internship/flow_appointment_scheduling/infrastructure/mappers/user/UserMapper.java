package com.internship.flow_appointment_scheduling.infrastructure.mappers.user;

import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.infrastructure.security.utils.PasswordEncoderComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = EmployeeDetailsMapper.class, implementationName = "UserMapperImpl")
public abstract class UserMapper {

  @Autowired
  private PasswordEncoderComponent passwordEncoderComponent;

  @Mapping(source = "employeeDetails", target = "employeeDetails")
  public abstract UserView toView(User entity);

  @Mapping(source = "password", target = "password", qualifiedByName = "encodePassword")
  public abstract User toEntity(UserPostRequest dto);

  public abstract void updateEntity(@MappingTarget User toUpdate, UserPutRequest dto);

  @Named("encodePassword")
  protected String encodePassword(String password) {
    return passwordEncoderComponent.encode(password);
  }
}