import {EventProps} from "react-big-calendar";
import {ShortAppointment} from "../../../models/appointment.types.ts";
import {Box, Typography} from "@mui/material";
import getStatusColor from "../getStatusColor.ts";

const WeekCustomEvent = ({event}: EventProps<ShortAppointment>) => {
  return (
    <Box
      bgcolor={getStatusColor(event.status)}
      color={'white'}
      borderRadius={1}
      height={'100%'}
      display={'flex'}
      alignItems={'center'}
      justifyContent={'center'}
    >
      <Typography variant="subtitle2">{event.serviceName}</Typography>
    </Box>
  );
};

export default WeekCustomEvent;