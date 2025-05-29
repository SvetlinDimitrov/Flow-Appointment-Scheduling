import {Appointment} from "../../models/appointment.types.ts";

export const formatAppointmentDate = (appointment: Appointment): string => {
  const startDate = new Date(appointment.startDate);
  const endDate = new Date(appointment.endDate);

  const day = startDate.toLocaleString('default', {weekday: 'long'});
  const dayOfMonth = startDate.getDate();
  const month = startDate.toLocaleString('default', {month: 'long'});
  const year = startDate.getFullYear();
  const startTime = startDate.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
  const endTime = endDate.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});

  return `${day}, ${dayOfMonth} ${month}, ${year} from ${startTime} to ${endTime}`;
};