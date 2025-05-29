import {Box, Button, Typography} from "@mui/material";
import {useNavigate} from "react-router-dom";
import {User} from "../../../../models/user.types.ts";

interface WelcomeSectionProps {
  user: User;
}

const WelcomeSection = ({user}: WelcomeSectionProps) => {
  const navigate = useNavigate();

  return (
    <Box p={2} pl={4} pr={4} mb={-3} bgcolor={'#f5f5f5'} justifyContent={'center'} alignItems={'center'}
         display={'flex'} gap={2} flexDirection={'column'}>
      <Typography variant={"h2"} color={"primary"} textAlign={'center'}>
        Welcome Back, {user.firstName}!
      </Typography>
      <Typography variant={"h5"} color={"textSecondary"} textAlign={'center'}>
        Manage Your Profile and Explore Our Services
      </Typography>
      <Typography variant={"h5"} color={"textSecondary"} textAlign={'center'}>
        Use the buttons below to quickly access your appointments or update your profile settings.
      </Typography>
      <Box display={'flex'} alignItems={'center'} gap={1} justifyContent={'center'}>
        <Button variant="outlined" color="primary"
                sx={{
                  fontSize: {
                    xs: '0.7rem',
                    sm: '0.9rem',
                  },
                }}
                onClick={() => navigate("/appointments")}>
          My Appointments
        </Button>
        <Button variant="outlined" color="primary"
                sx={{
                  fontSize: {
                    xs: '0.7rem',
                    sm: '0.9rem',
                  },
                }}
                onClick={() => navigate("/profile")}>
          Profile
        </Button>
      </Box>
    </Box>
  );
};

export default WelcomeSection;