import {UserRole} from "../user.types.ts";

export interface CreateUserRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface UpdateUserRequest {
  firstName: string;
  lastName: string;
}

export interface CreateUpdateUserAdminRequest {
  userRole: UserRole;
  salary: number;
  isAvailable: boolean;
  beginWorkingHour: string; // LocalDate in format YYYY-MM-DD
  endWorkingHour: string; // LocalDate in format YYYY-MM-DD
}

export interface HireStaffRequest {
  userInfo: CreateUserRequest;
  staffDetailsDto: CreateUpdateUserAdminRequest;
}
