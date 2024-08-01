package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  Page<UserView> getAll(Pageable pageable);

  UserView getById(Long id);

  UserView create(UserPostRequest userView);

  UserView update(Long id, UserPutRequest userView);

  void delete(Long id);

}
