import {Accordion, AccordionDetails, AccordionSummary, Stack, TextField, Typography} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {styled} from '@mui/system';
import {DateTime} from 'luxon';
import {User} from "../../../shared/models/user.types.ts";

const StyledAccordion = styled(Accordion)(({theme}) => ({
  backgroundColor: theme.palette.background.paper,
  borderRadius: theme.shape.borderRadius,
  '&:before': {
    display: 'none',
  },
}));

const StyledAccordionSummary = styled(AccordionSummary)(({theme}) => ({
  backgroundColor: theme.palette.flowBgColor.main,
  border: `1px solid ${theme.palette.divider}`,
  borderRadius: theme.shape.borderRadius,
  minHeight: 56,
  '&.Mui-expanded': {
    minHeight: 56,
  },
  '& .MuiAccordionSummary-content': {
    margin: theme.spacing(1, 0),
  },
}));

const StyledAccordionDetails = styled(AccordionDetails)(({theme}) => ({
  padding: theme.spacing(2),
  backgroundColor: theme.palette.flowBgColor.main,
}));

interface StaffDetailsAccordionProps {
  user: User;
}

const StaffDetailsAccordion = ({user}: StaffDetailsAccordionProps) => (
  <StyledAccordion>
    <StyledAccordionSummary expandIcon={<ExpandMoreIcon/>}>
      <Typography>Staff Details</Typography>
    </StyledAccordionSummary>
    {user.staffDetails &&
      <StyledAccordionDetails>
        <Stack spacing={2}>
          <TextField
            label="Role"
            defaultValue={user.role}
            disabled
          />
          <TextField
            label="Salary"
            defaultValue={user.staffDetails.salary}
            disabled
          />
          <TextField
            label="Completed Appointments"
            defaultValue={user.staffDetails.completedAppointments}
            disabled
          />
          <TextField
            label="Start Date"
            defaultValue={user.staffDetails.startDate.toString()}
            disabled
          />
          <TextField
            label="Is Available"
            defaultValue={user.staffDetails.isAvailable ? 'Yes' : 'No'}
            disabled
          />
          <TextField
            label="Begin Working Hour"
            defaultValue={DateTime.fromISO(user.staffDetails.beginWorkingHour.toString()).toFormat('HH:mm')}
            disabled
          />
          <TextField
            label="End Working Hour"
            defaultValue={DateTime.fromISO(user.staffDetails.endWorkingHour.toString()).toFormat('HH:mm')}
            disabled
          />
        </Stack>
      </StyledAccordionDetails>
    }
  </StyledAccordion>
);

export default StaffDetailsAccordion;