import {SyntheticEvent, useEffect, useRef, useState} from 'react';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Checkbox,
  Collapse,
  FormControlLabel,
  FormGroup,
  Grid,
  Typography
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {AppointmentStatus} from '../../../../shared/models/appointment.types.ts';
import getStatusColor from '../../../../shared/core/calendar/getStatusColor.ts';

interface AccordionGridProps {
  appointmentCounts: Record<AppointmentStatus, number>;
  selectedStatuses: AppointmentStatus[];
  handleStatusChange: (status: AppointmentStatus) => void;
}

const formatStatus = (status: string): string => {
  return status
    .toLowerCase()
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
};

const AccordionGridModal = ({appointmentCounts, selectedStatuses, handleStatusChange}: AccordionGridProps) => {
  const [expanded, setExpanded] = useState<string | false>(false);
  const accordionRef = useRef<HTMLDivElement | null>(null);

  const handleAccordionChange = (panel: string) => (_: SyntheticEvent, isExpanded: boolean) => {
    setExpanded(isExpanded ? panel : false);
  };

  const handleClickOutside = (event: MouseEvent) => {
    if (accordionRef.current && !accordionRef.current.contains(event.target as Node)) {
      setExpanded(false);
    }
  };

  useEffect(() => {
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <Grid
      container
      spacing={2}
      mb={3}
      width={'80%'}
      ref={accordionRef}
    >
      <Grid item xs={12}>
        <Accordion
          expanded={expanded === 'panel1'}
          onChange={handleAccordionChange('panel1')}
          sx={{position: 'relative', width: '100%'}}
        >
          <AccordionSummary expandIcon={<ExpandMoreIcon/>}>
            <Typography variant={'subtitle1'}>Select Statuses</Typography>
          </AccordionSummary>
          <AccordionDetails
            sx={{
              position: 'absolute',
              top: '100%',
              left: 0,
              right: 0,
              zIndex: 10,
              backgroundColor: 'white',
              boxShadow: 3,
              width: 'calc(100% - 48px)',
            }}
          >
            <Collapse in={expanded === 'panel1'}>
              <FormGroup>
                <Grid container spacing={2}>
                  {Object.values(AppointmentStatus).map((status) => (
                    <Grid
                      item
                      xs={12}
                      key={status}
                      display="flex"
                      alignItems="center"
                    >
                      <FormControlLabel
                        control={
                          <Checkbox
                            checked={selectedStatuses.includes(status)}
                            onChange={() => handleStatusChange(status)}
                            sx={{
                              '&.Mui-checked': {
                                color: getStatusColor(status),
                              },
                            }}
                          />
                        }
                        label={
                          <Grid container alignItems="center" spacing={1}>
                            <Grid item>
                              <Typography variant="subtitle1">
                                {formatStatus(status)}
                              </Typography>
                            </Grid>
                            <Grid item>
                              <Typography variant="subtitle1">
                                {appointmentCounts[status]}
                              </Typography>
                            </Grid>
                          </Grid>
                        }
                      />

                    </Grid>
                  ))}
                </Grid>
              </FormGroup>
            </Collapse>
          </AccordionDetails>
        </Accordion>
      </Grid>
    </Grid>
  );
};

export default AccordionGridModal;