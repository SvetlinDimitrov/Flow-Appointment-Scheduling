import { axiosInstance } from '../utils/axios-config/axiosInstance';
import Page from "../shared/models/api/shared/Page.ts";
import { Appointment, AppointmentCreate, AppointmentUpdate, ShortAppointment } from "../shared/models/appointment.types.ts";

const API_URL = '/appointments';

export const getAllAppointments = async (page: number, size: number) => {
  const params = { page, size };
  const response = await axiosInstance.get<Page<Appointment>>(API_URL, { params });
  return response.data;
};

export const getAllAppointmentsByServiceId = async (serviceId: number, page: number, size: number) => {
  const params = { page, size };
  const response = await axiosInstance.get<Page<Appointment>>(`${API_URL}/service/${serviceId}`, { params });
  return response.data;
};

export const getAllAppointmentsByServiceIdAndDate = async (serviceId: number, date: string) => {
  const params = { date };
  const response = await axiosInstance.get<ShortAppointment[]>(`${API_URL}/service/${serviceId}/short`, { params });
  return response.data;
};

export const getAllAppointmentsByUserId = async (userId: number, page: number, size: number) => {
  const params = { page, size };
  const response = await axiosInstance.get<Page<Appointment>>(`${API_URL}/user/${userId}`, { params });
  return response.data;
};

export const getAllAppointmentsByUserIdAndDate = async (userId: number, date: string) => {
  // Format date as 'YYYY-MM-DD'
  const params = { date };
  const response = await axiosInstance.get<ShortAppointment[]>(`${API_URL}/user/${userId}/short`, { params });
  return response.data;
};

export const getAppointmentById = async (id: number) => {
  const response = await axiosInstance.get<Appointment>(`${API_URL}/${id}`);
  return response.data;
};

export const createAppointment = async (appointment: AppointmentCreate) => {
  const response = await axiosInstance.post<Appointment>(API_URL, appointment);
  return response.data;
};

export const updateAppointment = async (id: number, dto: AppointmentUpdate) => {
  const response = await axiosInstance.put<Appointment>(`${API_URL}/${id}`, dto);
  return response.data;
};

export const deleteAppointment = async (id: number) => {
  await axiosInstance.delete(`${API_URL}/${id}`);
};