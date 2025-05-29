import {Box, Dialog, DialogContent, DialogTitle, Typography} from '@mui/material';
import MyCalendar from '../../../../shared/core/calendar/MyCalendar.tsx';
import BookToolbar from '../calendar-toolbars/BookToolbar.tsx';
import {User} from '../../../../shared/models/user.types.ts';
import {Service} from '../../../../shared/models/service.types.ts';
import {DateTime} from 'luxon';
import BookFormSection from './BookFormSection.tsx';
import {AppointmentCreate, AppointmentStatus} from "../../../../shared/models/appointment.types.ts";
import useGetUserQuery from "../../../../hooks/users/query/useGetUserQuery.ts";
import {useContext, useState} from "react";
import {UserAuthContext} from "../../../../shared/context/UserAuthContext.tsx";
import LoadingSpinner from "../../../../shared/core/loading/main-loader/LoadingSpinner.tsx";
import PageNotFound from "../../../../shared/core/not-found/PageNotFound.tsx";
import FullScreenLoader from "../../../../shared/core/loading/full-screen-loader/FullScreenLoader.tsx";
import {useConfirmationModal} from "../../../../shared/context/ConfirmationModalContext.tsx";
import useCreateAppointmentMutation from "../../../../hooks/appointments/mutation/useCreateAppointmentMutation.ts";
import {CalendarType, FetchType} from "../../../../shared/models/react-big-calendar.ts";

interface BookAppointmentModalProps {
  service: Service;
  staff: User;
  open: boolean;
  onClose: () => void;
}

const getWorkingHourValue = (workingHour?: DateTime): Date | undefined => {
  return workingHour ? DateTime.fromISO(workingHour.toString()).toJSDate() : undefined;
};

const BookAppointmentModal = ({service, staff, open, onClose}: BookAppointmentModalProps) => {
  const {userId} = useContext(UserAuthContext);

  const [isBooking, setIsBooking] = useState(false);

  const {data, error: isGetUserError, isLoading: isGetUserLoading} = useGetUserQuery(userId)
  const {openModal, closeModal} = useConfirmationModal();
  const createAppointmentMutation = useCreateAppointmentMutation();

  const startDateValue = getWorkingHourValue(staff.staffDetails?.beginWorkingHour);
  const endDateValue = getWorkingHourValue(staff.staffDetails?.endWorkingHour);

  const onSubmit = (data: AppointmentCreate) => {
    const onConfirm = () => {
      setIsBooking(true);
      createAppointmentMutation.mutate(data, {
        onSuccess: () => {
          onClose();
        },
        onSettled: () => {
          setIsBooking(false);
          closeModal();
        }
      });
    }

    openModal("Confirm Booking", "Are you sure you want to book this appointment?", onConfirm);
  };

  if (isGetUserLoading) return <LoadingSpinner/>
  if (isGetUserError) return <PageNotFound/>
  if (!userId || !staff || !service || !data) return null;

  return (
      <>
        <FullScreenLoader isLoading={isBooking}/>
        <Dialog
          open={open}
          onClose={onClose}
          maxWidth="lg"
          fullWidth
        >
          <DialogTitle>
            Book Appointment
          </DialogTitle>
          <DialogContent>
            <Box
              display="flex"
              p={1}
              minWidth={300}
              sx={{flexDirection: {xs: 'column', md: 'row'}}}
              gap={3}
            >
              <BookFormSection
                service={service}
                staff={staff}
                client={data}
                onSubmit={onSubmit}
              />
              <Box flex={1}>
                <Typography
                  variant="h5"
                  mb={2}
                >
                  Working Scheduler
                </Typography>
                <MyCalendar
                  filterByStatus={[AppointmentStatus.NOT_APPROVED , AppointmentStatus.APPROVED]}
                  calendarType={CalendarType.BOOK}
                  fetchId={staff.id}
                  fetchType={FetchType.USER}
                  CustomToolbar={BookToolbar}
                  width={'100%'}
                  height={'95%'}
                  startHourBoundary={startDateValue}
                  endHourBoundary={endDateValue}
                />
              </Box>
            </Box>
          </DialogContent>
        </Dialog>
      </>
  );
};

export default BookAppointmentModal;