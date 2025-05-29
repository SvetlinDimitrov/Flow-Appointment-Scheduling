import {User} from "./user.types.ts";
import {Service} from "./service.types.ts";

export enum AppointmentStatus {
  NOT_APPROVED = 'NOT_APPROVED',
  APPROVED = 'APPROVED',
  COMPLETED = 'COMPLETED',
  CANCELED = 'CANCELED'
}

export enum UpdateAppointmentStatus {
  APPROVED = 'APPROVED',
  COMPLETED = 'COMPLETED',
  CANCELED = 'CANCELED'
}

export interface Appointment {
  id: number;
  client: User;
  staff: User;
  startDate: Date;
  endDate: Date;
  status: AppointmentStatus;
  service: Service;
}

export interface ShortAppointment {
  id: number;
  serviceName: string;
  startDate: Date;
  endDate: Date;
  status: AppointmentStatus;
}

export interface AppointmentCreate {
  serviceId: number;
  clientEmail: string;
  staffEmail: string;
  date: string; // yyyy-MM-dd'T'HH:mm:ss
}

export interface AppointmentUpdate {
  status: UpdateAppointmentStatus;
}