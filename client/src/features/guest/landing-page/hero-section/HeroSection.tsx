import {Box, Typography} from '@mui/material';
import {styled} from "@mui/system";

const MainBox = styled(Box)(({theme}) => ({
  position: 'relative',
  height: '100vh',
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  color: 'white',
  textAlign: 'center',
  overflow: 'hidden',
  gap: theme.spacing(8),
  backgroundImage: 'url(/static/images/home/hero_section_bg.png)',
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  paddingLeft: theme.spacing(4),
  paddingRight: theme.spacing(4),
  '::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    zIndex: 1,
  },
  '& > *': {
    position: 'relative',
    zIndex: 2,
  },
}));

const HeroSection = () => {

  return (
    <MainBox>
      <Typography variant="h2" gutterBottom>
        Welcome to Flow: Your Wellness Journey Starts Here
      </Typography>
      <Typography variant="h5" gutterBottom>
        Book Appointments for Fitness, Spa, and Wellness Services with Ease
      </Typography>
    </MainBox>
  );
};

export default HeroSection;