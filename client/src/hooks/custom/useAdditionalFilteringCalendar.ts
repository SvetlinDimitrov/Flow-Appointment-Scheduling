import {useCallback, useState} from 'react';
import {AppointmentStatus} from "../../shared/models/appointment.types.ts";

const useAdditionalFilteringCalendar = () => {
  const [selectedStatuses, setSelectedStatuses] = useState<AppointmentStatus[]>([
    AppointmentStatus.NOT_APPROVED,
    AppointmentStatus.APPROVED,
    AppointmentStatus.COMPLETED,
    AppointmentStatus.CANCELED,
  ]);

  const [appointmentCounts, setAppointmentCounts] = useState<Record<AppointmentStatus, number>>({
    [AppointmentStatus.NOT_APPROVED]: 0,
    [AppointmentStatus.APPROVED]: 0,
    [AppointmentStatus.COMPLETED]: 0,
    [AppointmentStatus.CANCELED]: 0,
  });

  const handleStatusChange = (status: AppointmentStatus) => {
    setSelectedStatuses((prev) =>
      prev.includes(status) ? prev.filter((s) => s !== status) : [...prev, status]
    );
  };

  const updateAppointmentCounts = useCallback((newCounts: Record<AppointmentStatus, number>) => {
    setAppointmentCounts(newCounts);
  }, []);

  return {
    selectedStatuses,
    appointmentCounts,
    handleStatusChange,
    updateAppointmentCounts,
  };
};

export default useAdditionalFilteringCalendar;