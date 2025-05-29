import {Box, SelectChangeEvent, Typography} from '@mui/material';
import ServiceStatisticsHeader from './ServiceStatisticsHeader';
import { TimeRange } from "../../../../shared/models/service.types.ts";

interface NoActivitiesMessageProps {
  timeRange: TimeRange;
  handleTimeRangeChange: (event: SelectChangeEvent<TimeRange>) => void;
}

const NoActivitiesMessage = ({ timeRange, handleTimeRangeChange }: NoActivitiesMessageProps) => {
  return (
    <Box
      alignItems="center"
      flexDirection={'column'}
      bgcolor="background.paper"
      p={1}
      borderRadius={2}
      boxShadow={24}
      minWidth={400}
      height={800}
      width={'80%'}
    >
      <Box mt={2}>
        <ServiceStatisticsHeader
          timeRange={timeRange}
          handleTimeRangeChange={handleTimeRangeChange}
        />
      </Box>
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        flexDirection="column"
        height="60%"
      >
        <Typography variant="h6" component="h2" textAlign="center">
          No activities available
        </Typography>
      </Box>
    </Box>
  );
};

export default NoActivitiesMessage;