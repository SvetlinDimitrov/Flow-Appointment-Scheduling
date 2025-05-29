import {Box, Typography} from '@mui/material';
import MyCalendar from "../../../shared/core/calendar/MyCalendar.tsx";
import {useContext} from "react";
import {UserAuthContext} from "../../../shared/context/UserAuthContext.tsx";
import ClientCustomToolbar from "./calendar-toolbars/ClientCustomToolbar.tsx";
import {CalendarType, FetchType} from "../../../shared/models/react-big-calendar.ts";

const ClientAppointmentInfo = () => {
  const {userId} = useContext(UserAuthContext);

  if (!userId) return null;

  return (
    <>
      <Box
        display={"flex"}
           flexDirection={"column"}
           alignItems={"center"}
           justifyContent={"center"}
           height={"86.1vh"}
      >
        <Typography variant={"h5"} mb={5}>
          My Appointments
        </Typography>
        <MyCalendar
          calendarType={CalendarType.CLIENT}
          fetchId={userId}
          fetchType={FetchType.USER}
          CustomToolbar={ClientCustomToolbar}
          width={'90%'}
          height={'80%'}
        />
      </Box>
    </>
  );
};

export default ClientAppointmentInfo;