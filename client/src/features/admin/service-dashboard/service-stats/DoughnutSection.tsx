import {Grid, Typography} from '@mui/material';
import {MdEvent, MdPending, MdTrendingDown, MdTrendingUp} from 'react-icons/md';
import {Doughnut} from 'react-chartjs-2';
import {ArcElement, Chart as ChartJS, Legend, Tooltip} from 'chart.js';
import {AppointmentStatus, ShortAppointment} from '../../../../shared/models/appointment.types.ts';
import {Service} from '../../../../shared/models/service.types.ts';
import getStatusColor from "../../../../shared/core/calendar/getStatusColor.ts";

ChartJS.register(ArcElement, Tooltip, Legend);

interface DoughnutSectionProps {
  events: ShortAppointment[];
  service: Service;
}

const DoughnutSection = ({events, service}: DoughnutSectionProps) => {
  const statusCounts = events.reduce((acc, event) => {
    acc[event.status] = (acc[event.status] || 0) + 1;
    return acc;
  }, {} as Record<AppointmentStatus, number>);

  const totalProfit = (statusCounts[AppointmentStatus.COMPLETED] || 0) * service.price;
  const totalLosses = (statusCounts[AppointmentStatus.CANCELED] || 0) * service.price;
  const totalUndecided = ((statusCounts[AppointmentStatus.NOT_APPROVED] || 0) + (statusCounts[AppointmentStatus.APPROVED] || 0)) * service.price;

  const pieData = {
    labels: Object.keys(statusCounts).map(label => {
      return label
        .toLowerCase()
        .replace(/_/g, ' ')
        .replace(/\b\w/g, char => char.toUpperCase());
    }),
    datasets: [
      {
        data: Object.values(statusCounts),
        backgroundColor: Object.keys(statusCounts).map(status => getStatusColor(status as AppointmentStatus)),
        borderColor: Object.keys(statusCounts).map(status => getStatusColor(status as AppointmentStatus)),
        borderWidth: 1,
      },
    ],
    position: 'bottom',
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "right" as const,
      },
    },
  };

  return (
    <Grid
      item
      xs={12}
      display="flex"
      flexDirection="column"
      height="35%"
    >
      <Grid
        item
        xs={12}
        display="flex"
        justifyContent="center"
        height={'80%'}
        margin={'auto'}
      >
        <Doughnut data={pieData} options={options}/>
      </Grid>
      <Grid
        item
        xs={12}
        display="flex"
        justifyContent="center"
        alignItems="center"
        mt={2}
        gap={2}
      >
        <Grid item display="flex" alignItems="center">
          <MdEvent style={{color: 'blue', fontSize: '2rem'}}/>
          <Typography variant="h6" component="h2" gutterBottom>
            {events.length} Events
          </Typography>
        </Grid>
        <Grid item display="flex" alignItems="center">
          <MdTrendingUp style={{color: 'green', fontSize: '2rem'}}/>
          <Typography variant="h6" component="h2" gutterBottom>
            ${totalProfit}
          </Typography>
        </Grid>
        <Grid item display="flex" alignItems="center">
          <MdTrendingDown style={{color: 'red', fontSize: '2rem'}}/>
          <Typography variant="h6" component="h2" gutterBottom>
            ${totalLosses}
          </Typography>
        </Grid>
        <Grid item display="flex" alignItems="center">
          <MdPending style={{fontSize: '2rem'}}/>
          <Typography variant="h6" component="h2" gutterBottom>
            ${totalUndecided}
          </Typography>
        </Grid>
      </Grid>
    </Grid>
  );
};

export default DoughnutSection;