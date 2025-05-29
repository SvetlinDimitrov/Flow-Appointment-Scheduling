import {FormControl, Grid, InputLabel, MenuItem, Select, SelectChangeEvent, Typography} from '@mui/material';
import {TimeRange} from "../../../../shared/models/service.types.ts";

interface ServiceStatisticsHeaderProps {
  timeRange: TimeRange;
  handleTimeRangeChange: (event: SelectChangeEvent<TimeRange>) => void;
}

const ServiceStatisticsHeader = ({timeRange, handleTimeRangeChange}: ServiceStatisticsHeaderProps) => {
  return (
    <Grid
      item
      xs={12}
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      mb={2}
    >
      <Typography variant="h5" component="h2" gutterBottom>
        Service Statistics
      </Typography>
      <FormControl variant="outlined" size="small" sx={{mt: 1, minWidth: 200}}>
        <InputLabel id="time-range-label">Time Range</InputLabel>
        <Select
          variant={'outlined'}
          labelId="time-range-label"
          value={timeRange}
          label="Time Range"
          onChange={handleTimeRangeChange}
        >
          <MenuItem value={TimeRange.THIRTY_DAYS_BACK}>30 Days Back</MenuItem>
          <MenuItem value={TimeRange.THIRTY_DAYS_FRONT}>30 Days Front</MenuItem>
        </Select>
      </FormControl>
    </Grid>
  );
};

export default ServiceStatisticsHeader;