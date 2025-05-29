import {Box, Dialog, DialogContent, DialogTitle, Grid} from '@mui/material';
import HeaderAppointmentSection from './header/HeaderAppointmentSection.tsx';
import UserInfoSection from './user-info/UserInfoSection.tsx';
import ServiceDetailSection from './service-detail/ServiceDetailSection.tsx';
import MapComponent from '../../../../shared/core/map/MapComponent.tsx';
import {Appointment} from '../../../../shared/models/appointment.types.ts';

interface ClientAppointmentDetailsProps {
  appointment: Appointment;
  cancelAppointment: () => void;
  bookAgain: () => void;
  open: boolean;
  onClose: () => void;
}

const ClientAppointmentDetails = (
  {
    appointment,
    cancelAppointment,
    bookAgain,
    open,
    onClose
  }: ClientAppointmentDetailsProps) => {

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle variant={'h5'}>
        Appointment Details
      </DialogTitle>
      <DialogContent sx={{padding: 2, minWidth: 230}}>
        <Grid container>
          <Grid
            item
            xs={12}
            border={'1px solid black'}
          >
            <HeaderAppointmentSection
              appointment={appointment}
              cancelAppointment={cancelAppointment}
            />
          </Grid>
          <Grid
            item
            xs={12}
            md={6}
            border={'1px solid black'}
          >
            <UserInfoSection user={appointment.staff}/>
            <Box
              display={'flex'}
              alignItems={'center'}
              borderTop={'1px solid black'}
              sx={{
                borderBottom: {
                  md: '0px solid black',
                  xs: '1px solid black'
                }
              }}
            >
              <MapComponent
                position={[51.505, -0.09]}
                style={{height: '180px', width: '100%', margin: 'auto'}}
              />
            </Box>
          </Grid>
          <Grid
            item
            xs={12}
            md={6}
            border={'1px solid black'}
          >
            <ServiceDetailSection
              service={appointment.service}
              bookAgain={bookAgain}/>
          </Grid>
        </Grid>
      </DialogContent>
    </Dialog>
  );
};

export default ClientAppointmentDetails;