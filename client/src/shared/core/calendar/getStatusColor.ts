import {AppointmentStatus} from "../../models/appointment.types.ts";

const getStatusColor = (status: AppointmentStatus): string => {
  switch (status) {
    case AppointmentStatus.NOT_APPROVED:
      return '#9e9e9e'; // Gray
    case AppointmentStatus.APPROVED:
      return '#4caf50'; // Green
    case AppointmentStatus.COMPLETED:
      return '#2196f3'; // Blue
    case AppointmentStatus.CANCELED:
      return '#f44336'; // Red
  }
};

export default getStatusColor;