import {Box, Card, CardContent, Divider, IconButton, Menu, MenuItem, MenuItemProps, Typography} from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import {MouseEvent, useState} from "react";
import {Service} from "../../../../shared/models/service.types.ts";
import AdminButtons from "./AdminButtons.tsx";
import CardBody from "./CardBody.tsx";

const StyledMenuItem = (props: MenuItemProps) => {
  return (
    <MenuItem
      sx={{
        "&:hover": {
          color: "primary.main"
        },
      }}
      {...props}
    />
  );
};


interface ServiceCardProps {
  selectedService: Service;
  handleViewEmployees: () => void;
  handleDeleteService: () => void;
  handleUpdateService: () => void;
  handleAppointments: () => void;
  handleStatistics: () => void;
}

const ServiceCard = (
  {
    selectedService,
    handleViewEmployees,
    handleDeleteService,
    handleUpdateService,
    handleAppointments,
    handleStatistics
  }: ServiceCardProps) => {

  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const handleClick = (event: MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <Box>
      <Card
        sx={{
          width: '350px',
          margin: "auto",
          boxShadow: 3,
          padding: 2,
          borderRadius: 2,
          position: 'relative'
        }}>

        <IconButton
          onClick={handleClick}
          sx={{position: 'absolute', top: 8, right: 8}}
        >
          <MoreVertIcon/>
        </IconButton>

        <Menu
          anchorEl={anchorEl}
          open={open}
          onClose={handleClose}
        >
          <StyledMenuItem
            onClick={() => {
              handleViewEmployees();
              handleClose();
            }}
          >
            Staff
          </StyledMenuItem>

          <StyledMenuItem
            onClick={() => {
              handleAppointments();
              handleClose();
            }}
          >
            Events
          </StyledMenuItem>
          <StyledMenuItem
            onClick={() => {
              handleStatistics();
              handleClose();
            }}
          >
            Statistics
          </StyledMenuItem>
        </Menu>
        <CardContent>
          <Typography
            variant="h5"
            fontWeight="bold"
            textAlign="center"
            noWrap
            overflow="hidden"
            textOverflow="ellipsis"
          >
            {selectedService.name}
          </Typography>
          <Typography
            variant="subtitle2"
            margin="auto"
            textAlign="center"
            color="gray"
            fontStyle="italic"
            marginTop={1}
            noWrap
            overflow="hidden"
            textOverflow="ellipsis"
          >
            {selectedService.description}
          </Typography>
          <Divider sx={{marginY: 2}}/>
          <CardBody selectedService={selectedService}/>
        </CardContent>
        <Divider sx={{marginY: 2}}/>
        <Box display="flex" justifyContent="space-around" padding={2}>
          <AdminButtons
            handleUpdateService={handleUpdateService!}
            handleDeleteService={handleDeleteService!}
          />
        </Box>
      </Card>
    </Box>
  );
};

export default ServiceCard;