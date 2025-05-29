import {Box, Button, Typography} from '@mui/material';
import {Service} from "../../../../../shared/models/service.types.ts";
import {AccessTime, LocationOn, MonetizationOn} from '@mui/icons-material';
import {Duration} from "luxon";

interface ServiceDetailSectionProps {
  service: Service;
  bookAgain: () => void;
}

const ServiceDetailSection = ({service, bookAgain}: ServiceDetailSectionProps) => {
  return (
    <Box
      display={'flex'}
      flexDirection={'column'}
      justifyContent={'center'}
      alignItems={'center'}
      gap={2}
      p={1}
      sx={{marginTop: {xs: 3, md: 0}}}
    >
      <Typography variant="h6" fontWeight={'bold'}>
        {service.name}
      </Typography>
      <Typography variant="h6" textAlign={'center'}>
        {service.description}
      </Typography>
      <Box
        display={'flex'}
        alignItems={'center'}
        gap={1}
      >
        <AccessTime/>
        <Typography variant="subtitle1" textAlign={'center'}>
          {Duration.fromISO(service.duration).as('minutes')} minutes
        </Typography>
      </Box>
      <Box
        display={'flex'}
        alignItems={'center'}
        gap={1}
      >
        <LocationOn/>
        <Typography variant="subtitle1" textAlign={'center'}>
          {service.workSpace.name}
        </Typography>
      </Box>
      <Box
        display={'flex'}
        alignItems={'center'}
        gap={1}
      >
        <MonetizationOn/>
        <Typography variant="subtitle1" textAlign={'center'}>
          ${service.price}
        </Typography>
      </Box>
      <Box display={'flex'}>
        <Button
          variant="contained"
          color="primary"
          size={'small'}
          aria-hidden={false}
          onClick={bookAgain}
        >
          Book again
        </Button>
      </Box>
    </Box>
  );
};

export default ServiceDetailSection;