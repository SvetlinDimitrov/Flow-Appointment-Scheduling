import { Box, Button, Typography } from "@mui/material";

interface WelcomeServiceSectionProps {
  onCreateService: () => void;
}

const WelcomeServiceSection = ({ onCreateService }: WelcomeServiceSectionProps) => {
  return (
    <Box
      display="flex"
      justifyContent="space-around"
      alignItems="center"
      gap={2}
      sx={{
        flexDirection: { xs: 'column', md: 'row' },
      }}
    >
      <Typography variant="h4">
        Service Dashboard
      </Typography>
      <Button
        variant="outlined"
        color="primary"
        onClick={onCreateService}
      >
        Create Service
      </Button>
    </Box>
  );
};

export default WelcomeServiceSection;