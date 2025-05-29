import {styled} from "@mui/system";
import {Avatar, Box, Typography} from "@mui/material";

const MainWrapper = styled(Box)(({theme}) => ({
  textAlign: 'center',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  flexDirection: 'column',
  width: '80%',
  [theme.breakpoints.up('sm')]: {
    width: '80%',
  },
  [theme.breakpoints.up('md')]: {
    width: '40%',
  },
  [theme.breakpoints.up('lg')]: {
    width: '25%',
  },
  gap: theme.spacing(1),
  border: '1px solid #ccc',
  borderRadius: theme.shape.borderRadius,
  padding: theme.spacing(2),
  boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'
}));

const StyledAvatar = styled(Avatar)(({theme}) => ({
  width: 150,
  height: 150,
  marginBottom: theme.spacing(2)
}));

interface TeamMemberProps {
  name: string;
  role: string;
  description: string;
  imageSrc: string;
}

const TeamMember = ({name, role, description, imageSrc}: TeamMemberProps) => {
  return (
    <MainWrapper>
      <StyledAvatar src={imageSrc} alt={name}/>
      <Typography variant={"h5"} fontWeight={"bold"}>{name}</Typography>
      <Typography variant={"subtitle1"} color={'black'}>{role}</Typography>
      <Typography variant={"subtitle2"} color={'dimgray'}>{description}</Typography>
    </MainWrapper>
  );
};

export default TeamMember;