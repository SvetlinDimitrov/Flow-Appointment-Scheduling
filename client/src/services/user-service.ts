import {axiosInstance} from '../utils/axios-config/axiosInstance';
import Page from "../shared/models/api/shared/Page.ts";
import {User, UserRole} from "../shared/models/user.types.ts";
import {
  CreateUpdateUserAdminRequest,
  CreateUserRequest,
  HireStaffRequest,
  UpdateUserRequest
} from "../shared/models/api/users.ts";
import {UserPasswordUpdate} from "../shared/models/api/auth.ts";

export const getAllUsers = async (
  page: number, size: number,
  userRole: UserRole, sort?: string, search?: string | null): Promise<Page<User>> => {
  const response = await axiosInstance.get(`/users`, {
    params: {
      page,
      size,
      userRole,
      sort: sort? sort : undefined,
      search: search? search : undefined
    }
  });
  return response.data;
};

export const getAllUsersByServiceId = async (page: number, size: number, serviceId: number): Promise<Page<User>> => {
  const response = await axiosInstance.get(`/users/service/${serviceId}`, {
    params: {
      page,
      size
    }
  });
  return response.data;
};

export const getUserById = async (id: number): Promise<User> => {
  const response = await axiosInstance.get(`/users/${id}`);
  return response.data;
};

export const createUser = async (user: CreateUserRequest): Promise<User> => {
  const response = await axiosInstance.post(`/users`, user);
  return response.data;
};

export const updateUser = async (id: number, user: UpdateUserRequest): Promise<User> => {
  const response = await axiosInstance.put(`/users/${id}`, user);
  return response.data;
};

export const deleteUser = async (id: number) => {
  const response = await axiosInstance.delete(`/users/${id}`);
  return response.status;
};

export const modifyStaff = async (id: number, modifyDto: CreateUpdateUserAdminRequest): Promise<User> => {
  const response = await axiosInstance.put(`/users/${id}/staff`, modifyDto);
  return response.data;
};

export const hireStaff = async (hireDto: HireStaffRequest): Promise<User> => {
  const response = await axiosInstance.post(`/users/hire`, hireDto);
  return response.data;
};

export const userResetPasswordWithAuth = async (userPasswordUpdate: UserPasswordUpdate): Promise<User> => {
  const response = await axiosInstance.put(`/users/reset-password`, userPasswordUpdate);
  return response.data;
};

export const userResetPasswordWithCustomAuth = async (userPasswordUpdate: UserPasswordUpdate, jwtToken: string): Promise<User> => {
  const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/users/reset-password`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${jwtToken}`
    },
    body: JSON.stringify(userPasswordUpdate)
  });

  if (!response.ok) {
    throw new Error('Failed to reset password');
  }

  return response.json();
};