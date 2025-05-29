import {Box, Typography} from '@mui/material';
import {ShortAppointment} from '../../../models/appointment.types.ts';
import {EventProps} from "react-big-calendar";
import getStatusColor from "../getStatusColor.ts";

const DayCustomEvent = ({event}: EventProps<ShortAppointment>) => {

  const startTime = new Date(event.startDate).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
  const endTime = new Date(event.endDate).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});

  return (
    <Box
      bgcolor={getStatusColor(event.status)}
      color={'white'}
      display={'flex'}
      flexDirection={'column'}
      borderRadius={1}
      height={'100%'}
      p={0.2}
    >
      <Typography variant="caption">{startTime} - {endTime}</Typography>
      <Typography variant="caption">{event.serviceName}</Typography>
    </Box>
  );
};

export default DayCustomEvent;