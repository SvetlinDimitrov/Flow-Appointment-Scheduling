package com.intership.flow_appointment_scheduling.feature.user.service;

import com.intership.flow_appointment_scheduling.feature.user.dto.UserPostRequest;
import com.intership.flow_appointment_scheduling.feature.user.dto.UserPutRequest;
import com.intership.flow_appointment_scheduling.feature.user.dto.UserView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  Page<UserView> getAll(Pageable pageable);

  UserView getById(Long id);

  UserView create(UserPostRequest userView);

  UserView update(Long id, UserPutRequest userView);

  void delete(Long id);

}
