package com.internship.flow_appointment_scheduling.infrastructure.mappers.user;

import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenView;
import com.internship.flow_appointment_scheduling.infrastructure.security.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", implementationName = "RefreshTokenMapperImpl")
public interface RefreshTokenMapper {

  @Mapping(target = "token", source = "id")
  @Mapping(target = "expirationTime", source = "expiryDate")
  RefreshTokenView toView(RefreshToken entity);
}
