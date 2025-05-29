import {Box, Chip, Modal, Typography} from '@mui/material';
import AccordionGridModal from "./calendar-modal/AccordionGridModal.tsx";
import MyCalendar from "../../../shared/core/calendar/MyCalendar.tsx";
import {CalendarType, FetchType} from "../../../shared/models/react-big-calendar.ts";
import useAdditionalFilteringCalendar from "../../../hooks/custom/useAdditionalFilteringCalendar.ts";
import AdminStaffCustomToolbar from "../appointment-staff/calendar-toolbars/AdminStaffCustomToolbar.tsx";

interface ServiceCalendarModalProps {
  open: boolean;
  type: FetchType;
  name: string;
  id: number;
  handleClose: () => void;
}

const AdminCalendarModal = ({open, handleClose, type, id , name}: ServiceCalendarModalProps) => {

  const {
    selectedStatuses,
    appointmentCounts,
    handleStatusChange,
    updateAppointmentCounts
  } = useAdditionalFilteringCalendar();

  return (
    <Modal
      open={open}
      onClose={handleClose}
      aria-labelledby="modal-title"
      aria-describedby="modal-description"
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backdropFilter: 'blur(3px)',
      }}
    >
      <Box display={"flex"}
           flexDirection={"column"}
           alignItems={"center"}
           justifyContent={"center"}
           height={"86.1vh"}
           width={'80%'}
           minWidth={'300px'}
           bgcolor={"#f5f5f5"}
           p={3}
           borderRadius={2}
           boxShadow={24}
      >
        <Box
          display="flex"
          flexDirection="row"
          alignItems="center"
          mb={3}
        >
          <Typography variant={"h5"} mr={2}>
            {name} Calendar
          </Typography>
          <Chip
            label={`Total: ${selectedStatuses.reduce((total, status) => total + appointmentCounts[status], 0)}`}
            color="primary"
            variant="outlined"
          />
        </Box>
        <AccordionGridModal
          appointmentCounts={appointmentCounts}
          selectedStatuses={selectedStatuses}
          handleStatusChange={handleStatusChange}
        />
        <MyCalendar
          filterByStatus={selectedStatuses}
          calendarType={CalendarType.ADMIN}
          fetchId={id}
          fetchType={type}
          CustomToolbar={AdminStaffCustomToolbar}
          width={'90%'}
          height={'80%'}
          updateAppointmentCounts={updateAppointmentCounts}
        />
      </Box>
    </Modal>
  );
};

export default AdminCalendarModal;