import {styled} from "@mui/system";
import {Box, Paper} from "@mui/material";

export const UserMainWrapper = styled(Box)(({theme}) => ({
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  height: '80.9vh',
  padding: theme.spacing(2),
  paddingRight: theme.spacing(4),
  paddingLeft: theme.spacing(4),
}));

export const UserSecondWrapper = styled(Paper)(({theme}) => ({
  padding: theme.spacing(4),
  borderRadius: theme.shape.borderRadius,
  width: '60%',
}));

