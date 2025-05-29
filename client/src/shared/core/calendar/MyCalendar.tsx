import {ComponentType, useEffect, useState} from 'react';
import {Calendar, momentLocalizer, ToolbarProps, View} from 'react-big-calendar';
import moment from 'moment';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import {AppointmentStatus, ShortAppointment, UpdateAppointmentStatus} from '../../models/appointment.types.ts';
import './index.css';
import ContainerLoader from '../loading/container-loader/ContainerLoader.tsx';
import useGetAllAppointmentsShort from '../../../hooks/appointments/query/useGetAllAppointmentsShortByUserId.ts';
import {Box} from '@mui/material';
import {CalendarType, FetchType} from '../../models/react-big-calendar.ts';
import useGetAppointmentByIdQuery from '../../../hooks/appointments/query/useGetAppointmentByIdQuery.ts';
import {useConfirmationModal} from '../../context/ConfirmationModalContext.tsx';
import useUpdateAppointmentMutation from '../../../hooks/appointments/mutation/useUpdateAppointmentMutation.ts';
import MonthCustomEvent from './events/MonthCustomEvent.tsx';
import DayCustomEvent from './events/DayCustomEvent.tsx';
import WeekCustomEvent from './events/WeekCustomEvent.tsx';
import BookAppointmentModal from '../../../features/appointment/appointment-client/book-modal/BookAppointmentModal.tsx';
import AppointmentDetailsPopup from './AppointmentDetails.tsx';
import useDeleteAppointmentMutation from '../../../hooks/appointments/mutation/useDeleteAppointmentMutation.ts';
import AgendaCustomView from "./events/AgendaCustomView.tsx";
import ErrorPage from "../error-page/ErrorPage.tsx";

const localize = momentLocalizer(moment);

interface MyCalendarProps {
  CustomToolbar: ComponentType<ToolbarProps<ShortAppointment>>;
  filterByStatus?: AppointmentStatus[];
  height: number | string;
  width: number | string;
  startHourBoundary?: Date
  endHourBoundary?: Date;
  startDate?: Date;
  fetchId: number;
  fetchType: FetchType;
  calendarType: CalendarType;
  updateAppointmentCounts?: (newCounts: Record<AppointmentStatus, number>) => void;
}

const MyCalendar = (
  {
    filterByStatus = [
      AppointmentStatus.APPROVED, AppointmentStatus.COMPLETED,
      AppointmentStatus.CANCELED, AppointmentStatus.NOT_APPROVED
    ],
    startDate = new Date(),
    calendarType,
    CustomToolbar,
    height,
    width,
    startHourBoundary,
    endHourBoundary,
    fetchId,
    fetchType,
    updateAppointmentCounts,
  }: MyCalendarProps) => {
  const [view, setView] = useState<View>('day');
  const [range, setRange] = useState<{ start: Date; end: Date }>({start: startDate, end: startDate});
  const [currentAppointmentId, setCurrentAppointmentId] = useState<number | null>(null);
  const [isBooking, setIsBooking] = useState(false);

  const {
    data: appointment,
    isLoading: appointmentLoading,
    error: appointmentError,
  } = useGetAppointmentByIdQuery(currentAppointmentId);
  const {
    data: events = [],
    isLoading: allAppointmentsLoading,
    error: allAppointmentsError,
    isFetching: isFetchingAppointments,
  } = useGetAllAppointmentsShort(fetchId, range.start, range.end, fetchType);
  const {openModal, closeModal} = useConfirmationModal();
  const updateAppointmentMutation = useUpdateAppointmentMutation();
  const deleteAppointmentMutation = useDeleteAppointmentMutation();

  const handleOpenAppointmentDetails = (e: ShortAppointment) => {
    if (calendarType !== CalendarType.BOOK) setCurrentAppointmentId(e.id);
  };

  const modifyAppointment = (appointmentId: number, status: UpdateAppointmentStatus) => {
    const onConfirm = () => {
      updateAppointmentMutation.mutate(
        {id: appointmentId, appointment: {status}},
        {
          onSettled: () => {
            setCurrentAppointmentId(null);
            closeModal();
          },
        }
      );
    };

    openModal('Cancel Appointment', `Are you sure you want to cancel this appointment?`, onConfirm);
  };

  const deleteAppointment = (appointmentId: number) => {
    const onConfirm = () => {
      deleteAppointmentMutation.mutate(appointmentId, {
        onSettled: () => {
          setCurrentAppointmentId(null);
          closeModal();
        },
      });
    };
    openModal('Delete Appointment', `Are you sure you want to delete this appointment?`, onConfirm);
  };

  const handleRangeChange = (range: { start: Date; end: Date } | Date[]) => {
    let startDate, endDate;

    if (Array.isArray(range)) {
      startDate = range[0];
      endDate = range[range.length - 1];
    } else {
      startDate = range.start;
      endDate = range.end;
    }

    setRange({start: startDate, end: endDate});
  };

  useEffect(() => {
    const updateCounts = () => {
      const newCounts = {
        [AppointmentStatus.NOT_APPROVED]: 0,
        [AppointmentStatus.APPROVED]: 0,
        [AppointmentStatus.COMPLETED]: 0,
        [AppointmentStatus.CANCELED]: 0,
      };

      events.forEach((event) => {
        newCounts[event.status] = (newCounts[event.status] || 0) + 1;
      });

      if (updateAppointmentCounts) {
        updateAppointmentCounts(newCounts);
      }
    };

    updateCounts();
  }, [events, updateAppointmentCounts]);

  if (allAppointmentsError || appointmentError) return <ErrorPage/>;

  return (
    <>
      <Box position="relative" style={{height, width}}>
        {(allAppointmentsLoading || isFetchingAppointments) && (
          <Box position="absolute" width="100%" display="flex">
            <ContainerLoader height={200}/>
          </Box>
        )}
        <Calendar
          localizer={localize}
          events={events.filter((event) => filterByStatus.includes(event.status))}
          startAccessor={(event: ShortAppointment) => new Date(event.startDate)}
          endAccessor={(event: ShortAppointment) => new Date(event.endDate)}
          titleAccessor={(event: ShortAppointment) => event.serviceName}
          onView={(newView) => setView(newView)}
          view={view}
          step={20}
          timeslots={3}
          min={startHourBoundary && new Date(startHourBoundary)}
          max={endHourBoundary && new Date(endHourBoundary)}
          onRangeChange={handleRangeChange}
          onSelectEvent={handleOpenAppointmentDetails}
          components={{
            toolbar: CustomToolbar,
            month: {event: MonthCustomEvent},
            day: {event: DayCustomEvent},
            week: {event: WeekCustomEvent},
            agenda: {event: AgendaCustomView},
          }}
        />
      </Box>
      <AppointmentDetailsPopup
        calendarType={calendarType}
        currentAppointmentId={currentAppointmentId}
        appointment={appointment}
        appointmentLoading={appointmentLoading}
        modifyAppointment={modifyAppointment}
        deleteAppointment={deleteAppointment}
        setIsBooking={setIsBooking}
        setCurrentAppointmentId={setCurrentAppointmentId}
      />
      {appointment && isBooking && (
        <BookAppointmentModal
          open={isBooking}
          onClose={() => setIsBooking(false)}
          service={appointment.service}
          staff={appointment.staff}
        />
      )}
    </>
  );
};

export default MyCalendar;