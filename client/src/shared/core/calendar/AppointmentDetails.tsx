import {CalendarType} from '../../models/react-big-calendar.ts';
import AdminEventView from '../../../features/appointment/appoitment-admin/appointment-view/AdminEventView.tsx';
import StaffEventView from '../../../features/appointment/appointment-staff/appointment-view/StaffEventView.tsx';
import ClientAppointmentDetails
  from '../../../features/appointment/appointment-client/detailed-appoitment/ClientAppointmentDetails.tsx';
import {Appointment, UpdateAppointmentStatus} from '../../models/appointment.types.ts';

interface AppointmentDetailsProps {
  calendarType: CalendarType;
  currentAppointmentId: number | null;
  appointment: Appointment | undefined;
  appointmentLoading: boolean;
  modifyAppointment: (appointmentId: number, status: UpdateAppointmentStatus) => void;
  deleteAppointment: (appointmentId: number) => void;
  setIsBooking: (isBooking: boolean) => void;
  setCurrentAppointmentId: (id: number | null) => void;
}

const AppointmentDetails = (
  {
    calendarType,
    currentAppointmentId,
    appointment,
    appointmentLoading,
    modifyAppointment,
    deleteAppointment,
    setIsBooking,
    setCurrentAppointmentId
  }: AppointmentDetailsProps) => {
  if (!currentAppointmentId || appointmentLoading || !appointment) return null;

  return (
    <>
      {calendarType === CalendarType.ADMIN && (
        <AdminEventView
          deleteAppointment={() => deleteAppointment(currentAppointmentId)}
          open={!!currentAppointmentId}
          onClose={() => setCurrentAppointmentId(null)}
          appointment={appointment}
          onAppointmentUpdate={(status: UpdateAppointmentStatus) =>
            modifyAppointment(currentAppointmentId, status)
          }
        />
      )}
      {calendarType === CalendarType.STAFF && (
        <StaffEventView
          open={!!currentAppointmentId}
          onClose={() => setCurrentAppointmentId(null)}
          appointment={appointment}
          onAppointmentUpdate={(status: UpdateAppointmentStatus) =>
            modifyAppointment(currentAppointmentId, status)
          }
        />
      )}
      {calendarType === CalendarType.CLIENT && (
        <ClientAppointmentDetails
          onClose={() => setCurrentAppointmentId(null)}
          open={!!currentAppointmentId}
          appointment={appointment}
          cancelAppointment={() =>
            modifyAppointment(currentAppointmentId, UpdateAppointmentStatus.CANCELED)
          }
          bookAgain={() => setIsBooking(true)}
        />
      )}
    </>
  );
};

export default AppointmentDetails;