import {DateTime} from 'luxon';

export enum UserRole {
  ADMINISTRATOR = 'ADMINISTRATOR',
  EMPLOYEE = 'EMPLOYEE',
  CLIENT = 'CLIENT'
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  staffDetails: StaffDetails | null;
}

export interface StaffDetails {
  salary: number;
  profit: number;
  completedAppointments: number;
  startDate: Date,
  isAvailable: boolean;
  beginWorkingHour: DateTime;
  endWorkingHour: DateTime;
  serviceIds: number[];
}
