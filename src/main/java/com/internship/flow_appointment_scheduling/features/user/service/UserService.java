package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeHireDto;
import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeModifyDto;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  Page<UserView> getAll(Pageable pageable , UserRoles userRole);

  UserView getById(Long id);

  UserView create(UserPostRequest userView);

  UserView update(Long id, UserPutRequest userView);

  void delete(Long id);

  User findByEmail(String email);

  UserView hireEmployee(EmployeeHireDto userPostRequest);

  UserView modifyEmployee(Long id, EmployeeModifyDto userPutRequest);
}
