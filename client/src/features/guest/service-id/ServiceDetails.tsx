import {Box, Grid, List, ListItem, ListItemIcon, ListItemText, Typography} from '@mui/material';
import {AccessTime, Cancel, CheckCircle, LocationOn, MonetizationOn} from '@mui/icons-material';
import {Duration} from 'luxon';
import {Service} from '../../../shared/models/service.types.ts';

interface ServiceDetailsProps {
  service: Service;
}

const ServiceDetails = ({service}: ServiceDetailsProps) => {
  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
    >
      <Box textAlign="center">
        <Typography
          textAlign="center"
          variant="h2"
          gutterBottom
        >
          {service.name}
        </Typography>
        <Typography
          textAlign="center"
          variant="h6"
          color="textSecondary"
          gutterBottom
        >
          {service.description}
        </Typography>
        <Grid
          container
          spacing={2}
          mt={2}
          justifyContent="center">
          <Grid
            item
            xs={12}
            sm={6}
            display="flex"
            sx={{
              display: 'flex',
              justifyContent: {
                xs: 'center',
                sm: 'flex-end'
              }
            }}>
            <Box
              display="flex"
              justifyContent="center"
              alignItems="center"
              width={'300px'}
              height={'150px'}
              sx={{
                objectFit: 'cover',
                overflow: 'hidden',
                backgroundColor: '#f0f0f0',
                color: '#aaa',
                fontSize: '16px',
                borderRadius: '8px'
              }}
            >
              No Image Available
            </Box>
          </Grid>
          <Grid
            item
            xs={12}
            sm={6}
            sx={{
              display: 'flex',
              justifyContent: {
                xs: 'center',
                sm: 'flex-start'
              }
            }}
          >
            <List>
              <ListItem sx={{padding: '4px 0'}}>
                <ListItemIcon>
                  <MonetizationOn/>
                </ListItemIcon>
                <ListItemText primary={`$${service.price}`}/>
              </ListItem>
              <ListItem sx={{padding: '4px 0'}}>
                <ListItemIcon>
                  <AccessTime/>
                </ListItemIcon>
                <ListItemText primary={`${Duration.fromISO(service.duration).as('minutes')} minutes`}/>
              </ListItem>
              <ListItem sx={{padding: '4px 0'}}>
                <ListItemIcon>
                  {service.availability ? (
                    <CheckCircle sx={{color: 'green'}}/>
                  ) : (
                    <Cancel sx={{color: 'red'}}/>
                  )}
                </ListItemIcon>
                <ListItemText primary={service.availability ? 'Available' : 'Not Available'}/>
              </ListItem>
              <ListItem sx={{padding: '4px 0'}}>
                <ListItemIcon>
                  <LocationOn/>
                </ListItemIcon>
                <ListItemText primary={service.workSpace.name}/>
              </ListItem>
            </List>
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
};

export default ServiceDetails;