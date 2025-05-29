import {useContext} from "react";
import {Box, Typography} from "@mui/material";
import MyCalendar from "../../../shared/core/calendar/MyCalendar.tsx";
import AdminStaffCustomToolbar from "./calendar-toolbars/AdminStaffCustomToolbar.tsx";
import {UserAuthContext} from "../../../shared/context/UserAuthContext.tsx";
import {CalendarType, FetchType} from "../../../shared/models/react-big-calendar.ts";
import AccordionGridModal from "../appoitment-admin/calendar-modal/AccordionGridModal.tsx";
import useAdditionalFilteringCalendar from "../../../hooks/custom/useAdditionalFilteringCalendar.ts";

const StaffAppointmentInfo = () => {
  const {userId} = useContext(UserAuthContext);

  const {
    selectedStatuses,
    appointmentCounts,
    handleStatusChange,
    updateAppointmentCounts
  } = useAdditionalFilteringCalendar();

  if(!userId) return null;
  return (
    <>
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        height="86.1vh"
      >
        <Typography variant="h5" mb={5}>
          My Appointments
        </Typography>

        <AccordionGridModal
          appointmentCounts={appointmentCounts}
          selectedStatuses={selectedStatuses}
          handleStatusChange={handleStatusChange}
        />

        <MyCalendar
          filterByStatus={selectedStatuses}
          calendarType={CalendarType.STAFF}
          CustomToolbar={AdminStaffCustomToolbar}
          fetchId={userId}
          fetchType={FetchType.USER}
          width="90%"
          height="80%"
          updateAppointmentCounts={updateAppointmentCounts}
        />
      </Box>

    </>
  );
};

export default StaffAppointmentInfo;