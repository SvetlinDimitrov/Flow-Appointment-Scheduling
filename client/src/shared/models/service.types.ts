import {User} from "./user.types.ts";

export interface Service {
  id: number;
  name: string;
  description: string;
  price: number;
  duration: string; // duration in ISO 8601 format
  availability: boolean;
  workSpace: Location;
}

export interface Location{
  name: string;
  capacity: number;
}

export interface ServiceWithUsers {
  id: number;
  name: string;
  description: string;
  duration: number;
  price: number;
  place: Location;
  totalProfit: number;
  totalAppointments: number;
  employees: User[];
}

export enum TimeRange {
  THIRTY_DAYS_BACK = '30daysBack',
  THIRTY_DAYS_FRONT = '30daysFront',
}
