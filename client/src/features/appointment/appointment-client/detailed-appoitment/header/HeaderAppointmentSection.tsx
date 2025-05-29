import {Box, Button, Chip, Typography} from '@mui/material';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import {styled} from '@mui/system';
import {Appointment, AppointmentStatus} from "../../../../../shared/models/appointment.types.ts";
import {formatAppointmentDate} from "../../../../../shared/core/calendar/formatAppointmentDate.ts";

const CalendarIconContainer = styled(Box)(({theme}) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  width: '50px',
  height: '50px',
  borderRadius: '50%',
  backgroundColor: theme.palette.flowBgColor.main,
  marginLeft: theme.spacing(2),
}));

interface HeaderAppointmentSectionProps {
  appointment: Appointment;
  cancelAppointment: () => void;
}

const HeaderAppointmentSection = ({appointment, cancelAppointment}: HeaderAppointmentSectionProps) => {

  return (
    <Box
      display={'flex'}
      alignItems={'center'}
      gap={1}
      p={1}
      justifyContent={'space-around'}
      sx={{flexDirection: {sm: 'row', xs: 'column'}}}
    >
      <CalendarIconContainer
        sx={{display: {sm: 'flex', xs: 'none'}}}>
        <CalendarTodayIcon fontSize="large"/>
      </CalendarIconContainer>
      <Box
        display={'flex'}
        flexDirection={'column'}
      >
        <Typography
          variant="h5"
          fontWeight={'bold'}
          mb={2}
          textAlign={'center'}>
          Scheduled appointment for
        </Typography>
        <Typography
          variant="subtitle2"
          textAlign={'center'}
        >
          {formatAppointmentDate(appointment)}
        </Typography>
      </Box>
      <Box
        display={'flex'}
        flexDirection={'column'}
        gap={2}
      >
        <Chip
          label={`Status: ${appointment.status}`}
          color="primary"
          variant="outlined"
          sx={{marginTop: 1}}
        />
        {appointment.status !== AppointmentStatus.CANCELED &&
          appointment.status !== AppointmentStatus.COMPLETED && (
          <Button
            variant="outlined"
            color="secondary"
            onClick={cancelAppointment}
            aria-hidden={false}
            sx={{
              marginLeft: 'auto',
              marginRight: 2,
              margin: {
                sm: 0,
                xs: 'auto'
              }
            }}
            size={'small'}
          >
            Cancel
          </Button>
        )}
      </Box>
    </Box>
  );
};

export default HeaderAppointmentSection;