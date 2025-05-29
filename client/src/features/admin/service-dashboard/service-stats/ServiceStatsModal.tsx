import {useState} from 'react';
import {Box, Grid, Modal, SelectChangeEvent} from '@mui/material';
import DoughnutSection from './DoughnutSection';
import BarChartSection from './BarChartSection';
import useGetServiceByIdQuery from '../../../../hooks/services/query/useGetServiceByIdQuery.ts';
import LoadingSpinner from '../../../../shared/core/loading/main-loader/LoadingSpinner.tsx';
import ErrorPage from '../../../../shared/core/error-page/ErrorPage.tsx';
import useGetAllAppointmentsShort from '../../../../hooks/appointments/query/useGetAllAppointmentsShortByUserId.ts';
import {FetchType} from '../../../../shared/models/react-big-calendar.ts';
import ServiceStatisticsHeader from './ServiceStatisticsHeader';
import {TimeRange} from "../../../../shared/models/service.types.ts";
import ContainerLoader from "../../../../shared/core/loading/container-loader/ContainerLoader.tsx";
import NoActivitiesMessage from "./NoActivitiesMessage.tsx";

interface BarChartModalProps {
  open: boolean;
  onClose: () => void;
  serviceId: number;
}

const ServiceStatsModal = ({ open, onClose, serviceId }: BarChartModalProps) => {
  const [timeRange, setTimeRange] = useState(TimeRange.THIRTY_DAYS_BACK);

  const handleTimeRangeChange = (event: SelectChangeEvent<TimeRange>) => {
    setTimeRange(event.target.value as TimeRange);
  };

  const thirtyDaysBack = new Date();
  thirtyDaysBack.setDate(thirtyDaysBack.getDate() - 30);

  const thirtyDaysFront = new Date();
  thirtyDaysFront.setDate(thirtyDaysFront.getDate() + 30);

  const {
    data: serviceData,
    isLoading: serviceLoading,
    error: serviceError
  } = useGetServiceByIdQuery(serviceId.toString());

  const {
    data: events = [],
    isLoading: allAppointmentsLoading,
    error: allAppointmentsError,
  } = useGetAllAppointmentsShort(
    serviceId,
    timeRange === TimeRange.THIRTY_DAYS_BACK ? thirtyDaysBack : new Date(),
    timeRange === TimeRange.THIRTY_DAYS_FRONT ? new Date() : thirtyDaysFront,
    FetchType.SERVICE
  );

  if (serviceLoading) return <LoadingSpinner/>;
  if (serviceError || allAppointmentsError) return <ErrorPage />;

  if (!serviceData) return null;

  return (
    <Modal
      open={open}
      onClose={onClose}
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backdropFilter: 'blur(3px)',
      }}
    >
      {allAppointmentsLoading ?
        <Box
          bgcolor="background.paper"
          height={800}
          width={'80%'}
        >
          <ContainerLoader height={800}/>
        </Box>
        : events.length > 0 ?
          <Grid
            container
            spacing={2}
            bgcolor="background.paper"
            p={1}
            borderRadius={2}
            boxShadow={24}
            minWidth={400}
            height={800}
            width={'80%'}
          >
            <ServiceStatisticsHeader
              timeRange={timeRange}
              handleTimeRangeChange={handleTimeRangeChange}
            />
            <DoughnutSection
              events={events}
              service={serviceData}
            />
            <BarChartSection events={events}/>
          </Grid>
          :
          <NoActivitiesMessage
            timeRange={timeRange}
            handleTimeRangeChange={handleTimeRangeChange}
          />
      }
    </Modal>
  );
};

export default ServiceStatsModal;