import {Box, Grid, Typography} from "@mui/material";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";
import PlaceIcon from "@mui/icons-material/Place";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";
import {Duration} from "luxon";
import {Service} from "../../../../shared/models/service.types.ts";

interface CardBodyProps {
  selectedService: Service;
}

const CardBody = ({selectedService}: CardBodyProps) => (
  <Box>
    <Grid
      container
      spacing={2}
      alignItems="center"
    >
      <Grid item xs={12}>
        <Box display="flex" alignItems="center">
          <AccessTimeIcon sx={{mr: 1}}/>
          <Typography
            variant="body2"
            sx={{ml: 1}}
            noWrap
            overflow="hidden"
            textOverflow="ellipsis"
          >
            {Duration.fromISO(selectedService.duration).as('minutes')} min
          </Typography>
        </Box>
      </Grid>
      <Grid item xs={12}>
        <Box display="flex" alignItems="center">
          <AttachMoneyIcon sx={{mr: 1}}/>
          <Typography
            variant="body2"
            sx={{ml: 1}}
            noWrap
            overflow="hidden"
            textOverflow="ellipsis"
          >
            {selectedService.price} $
          </Typography>
        </Box>
      </Grid>
      <Grid item xs={12}>
        <Box display="flex" alignItems="center">
          <PlaceIcon sx={{mr: 1}}/>
          <Typography
            variant="body2"
            sx={{ml: 1}}
            noWrap
            overflow="hidden"
            textOverflow="ellipsis"
          >
            {selectedService.workSpace.name}
          </Typography>
        </Box>
      </Grid>
      <Grid item xs={12}>
        <Box display="flex" alignItems="center">
          {selectedService.availability ?
            <CheckCircleIcon sx={{mr: 1}}/> : <CancelIcon sx={{mr: 1}}/>
          }
          <Typography
            variant="body2"
            sx={{ml: 1}}
            noWrap
            overflow="hidden"
            textOverflow="ellipsis"
          >
            {selectedService.availability ? 'Yes' : 'No'}
          </Typography>
        </Box>
      </Grid>
    </Grid>
  </Box>
);

export default CardBody;