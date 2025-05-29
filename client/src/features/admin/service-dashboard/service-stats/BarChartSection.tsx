import {Grid} from '@mui/material';
import {Bar} from 'react-chartjs-2';
import {BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, Title, Tooltip} from 'chart.js';
import {AppointmentStatus, ShortAppointment} from '../../../../shared/models/appointment.types.ts';
import getStatusColor from '../../../../shared/core/calendar/getStatusColor.ts';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

interface BarChartSectionProps {
  events: ShortAppointment[];
}

interface GroupedEvents {
  [week: number]: {
    [status in AppointmentStatus]: number;
  };
}

const getWeekNumber = (date: Date) => {
  const startDate = new Date(date.getFullYear(), 0, 1);
  const days = Math.floor((date.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
  return Math.ceil((date.getDay() + 1 + days) / 7);
};

const BarChartSection = ({ events }: BarChartSectionProps) => {
  const groupedEvents: GroupedEvents = {};
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - 30);

  for (let i = 0; i < 5; i++) {
    const week = getWeekNumber(
      new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate() + i * 7)
    );
    groupedEvents[week] = {
      [AppointmentStatus.NOT_APPROVED]: 0,
      [AppointmentStatus.APPROVED]: 0,
      [AppointmentStatus.COMPLETED]: 0,
      [AppointmentStatus.CANCELED]: 0
    };
  }

  events.forEach(event => {
    const eventDate = new Date(event.startDate);
    if (eventDate >= startDate) {
      const week = getWeekNumber(eventDate);
      if (!groupedEvents[week]) {
        groupedEvents[week] = {
          [AppointmentStatus.NOT_APPROVED]: 0,
          [AppointmentStatus.APPROVED]: 0,
          [AppointmentStatus.COMPLETED]: 0,
          [AppointmentStatus.CANCELED]: 0
        };
      }
      groupedEvents[week][event.status]++;
    }
  });

  const labels = Object.keys(groupedEvents).map(week => `Week ${week}`);
  const datasets = [
    {
      label: 'Not Approved',
      data: Object.values(groupedEvents).map(week => week[AppointmentStatus.NOT_APPROVED]),
      backgroundColor: getStatusColor(AppointmentStatus.NOT_APPROVED) + '33',
      borderColor: getStatusColor(AppointmentStatus.NOT_APPROVED),
      borderWidth: 1,
    },
    {
      label: 'Approved',
      data: Object.values(groupedEvents).map(week => week[AppointmentStatus.APPROVED]),
      backgroundColor: getStatusColor(AppointmentStatus.APPROVED) + '33',
      borderColor: getStatusColor(AppointmentStatus.APPROVED),
      borderWidth: 1,
    },
    {
      label: 'Completed',
      data: Object.values(groupedEvents).map(week => week[AppointmentStatus.COMPLETED]),
      backgroundColor: getStatusColor(AppointmentStatus.COMPLETED) + '33',
      borderColor: getStatusColor(AppointmentStatus.COMPLETED),
      borderWidth: 1,
    },
    {
      label: 'Canceled',
      data: Object.values(groupedEvents).map(week => week[AppointmentStatus.CANCELED]),
      backgroundColor: getStatusColor(AppointmentStatus.CANCELED) + '33',
      borderColor: getStatusColor(AppointmentStatus.CANCELED),
      borderWidth: 1,
    },
  ];

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "top" as const,
      },
      title: {
        display: true,
        text: 'Appointments Bar Chart by Week',
      },
    },
  };

  return (
    <Grid item xs={12} height="50%">
      <Bar data={{labels, datasets}} options={options} />
    </Grid>
  );
};

export default BarChartSection;