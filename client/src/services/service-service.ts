import {axiosInstance} from '../utils/axios-config/axiosInstance';
import Page from "../shared/models/api/shared/Page.ts";
import {Service} from "../shared/models/service.types.ts";
import {ServiceDTO} from "../shared/models/api/services.ts";

const API_URL = '/services';

export const getAllServices = async (page: number, size: number, staffEmail?: string) => {
  const params = {page, size, staffEmail};
  const response = await axiosInstance.get<Page<Service>>(API_URL, {params});
  return response.data;
};

export const getServiceById = async (id: number) => {
  const response = await axiosInstance.get<Service>(`${API_URL}/${id}`);
  return response.data;
};

export const createService = async (service: ServiceDTO) => {
  const response = await axiosInstance.post<Service>(API_URL, service);
  return response.data;
};

export const assignStaffToService = async (id: number, staffEmail: string) => {
  const response = await axiosInstance.post<Service>(`${API_URL}/${id}/assign`, null, {
    params: {staffEmail}
  });
  return response.data;
};

export const unassignStaffFromService = async (id: number, staffEmail: string) => {
  const response = await axiosInstance.put<Service>(`${API_URL}/${id}/unassign`, null, {
    params: {staffEmail}
  });
  return response.data;
};

export const updateService = async (id: number, service: ServiceDTO) => {
  const response = await axiosInstance.put<Service>(`${API_URL}/${id}`, service);
  return response.data;
};

export const deleteService = async (id: number) => {
  await axiosInstance.delete(`${API_URL}/${id}`);
};

export const getAllWorkSpacesNames = async () => {
  const response = await axiosInstance.get<string[]>(`${API_URL}/workspaces`);
  return response.data;
};