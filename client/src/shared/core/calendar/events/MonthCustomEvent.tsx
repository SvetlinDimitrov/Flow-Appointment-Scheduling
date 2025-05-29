import {EventProps} from 'react-big-calendar';
import {Box, Typography} from '@mui/material';
import {ShortAppointment} from "../../../models/appointment.types.ts";
import getStatusColor from "../getStatusColor.ts";

const MonthCustomEvent = ({event}: EventProps<ShortAppointment>) => {
  return (
    <Box
      bgcolor={getStatusColor(event.status)}
      color={'white'}
      p={0.4}
      borderRadius={2}
      height={'100%'}
      overflow={'auto'}
      textOverflow={'ellipsis'}
      whiteSpace={'nowrap'}
    >
      <Typography variant="caption">{event.serviceName}</Typography>
    </Box>
  );
};

export default MonthCustomEvent;