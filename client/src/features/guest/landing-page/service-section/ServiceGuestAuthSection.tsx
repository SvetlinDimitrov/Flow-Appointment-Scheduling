import ServiceCard from "./service-card/ServiceCard.tsx";
import {styled} from "@mui/system";
import {Box, Pagination, Stack, Typography, useMediaQuery, useTheme} from "@mui/material";
import usePaginatedQuery from "../../../../hooks/custom/usePaginatedQuery.ts";
import {Service} from "../../../../shared/models/service.types.ts";
import useGetAllServicesQuery from "../../../../hooks/services/query/useGetAllServicesQuery.ts";
import {useEffect} from "react";
import ContainerLoader from "../../../../shared/core/loading/container-loader/ContainerLoader.tsx";
import ErrorPage from "../../../../shared/core/error-page/ErrorPage.tsx";

const MainWrapper = styled(Stack)(({theme}) => ({
  display: 'flex',
  flexDirection: 'column',
  textAlign: 'center',
  justifyContent: 'center',
  gap: theme.spacing(5),
  padding: theme.spacing(2),
  paddingLeft: theme.spacing(4),
  paddingRight: theme.spacing(4),
  marginTop: theme.spacing(5),
  marginBottom: theme.spacing(5),
  minHeight: '80vh',
}));

const CardsHolder = styled(Box)(({theme}) => ({
  display: 'flex',
  flexDirection: 'row',
  [theme.breakpoints.down('lg')]: {
    flexDirection: 'column',
  },
  justifyContent: 'center',
  gap: theme.spacing(5),
  alignItems: 'center',
}));

interface ServiceGuestAuthSectionProps {
  title: string;
  description?: string;
  buttonText: string;
}

const ServiceGuestAuthSection = ({title, description , buttonText}: ServiceGuestAuthSectionProps) => {
  const theme = useTheme();

  const isLg = useMediaQuery(theme.breakpoints.down('lg'));

  const servicesPerPage = isLg ? 1 : 3;

  const {
    data,
    isLoading,
    error,
    page,
    handlePageChange,
    setPage
  } = usePaginatedQuery<Service>(useGetAllServicesQuery, 0, servicesPerPage);

  useEffect(() => {
    setPage(0);
  }, [isLg, setPage]);

  if (error) return <ErrorPage/>;

  return (
    <MainWrapper>
      <Typography variant={"h2"} color={'#333'}>
        {title}
      </Typography>
      {description &&
      <Typography variant={"h5"} color={'#666'}>
        {description}
      </Typography>
      }
      {isLoading &&
        <ContainerLoader height={540}/>
      }
      {!isLoading && data && <>
      <CardsHolder>
        {data.content.map((service, index) => (
          <ServiceCard
            key={index}
            image={"https://via.placeholder.com/350x150/CCCCCC/CCCCCC"}
            alt={service.name}
            title={service.name}
            navigateTo={`/service/${service.id}`}
            buttonText={buttonText}
            serviceAvailable={service.availability}
          />
        ))}
      </CardsHolder>
        <Box display="flex" justifyContent="center" mt={2}>
          <Pagination
            count={data.totalPages}
            page={page + 1}
            onChange={(_, value) => handlePageChange(value)}
            color="primary"
            boundaryCount={isLg ? 0 : 1}
            siblingCount={isLg ? 0 : 1}
          />
        </Box>
      </>
      }
    </MainWrapper>
  );
};

export default ServiceGuestAuthSection;