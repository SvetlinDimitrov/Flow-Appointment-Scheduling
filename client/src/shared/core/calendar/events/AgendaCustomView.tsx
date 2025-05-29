import { Box, Typography } from '@mui/material';
import { ShortAppointment } from '../../../models/appointment.types.ts';
import getStatusColor from '../getStatusColor.ts';
import { EventProps } from 'react-big-calendar';

const AgendaCustomView = ({ event }: EventProps<ShortAppointment>) => {
  return (
    <Box
      display="flex"
      alignItems="center"
      borderRadius={1}
      height="100%"
      p={0.2}
      sx={{ cursor: 'pointer' }}
    >
      <Box
        width={16}
        height={16}
        bgcolor={getStatusColor(event.status)}
        mr={1}
      />
      <Typography variant="caption">{event.serviceName}</Typography>
    </Box>
  );
};

export default AgendaCustomView;