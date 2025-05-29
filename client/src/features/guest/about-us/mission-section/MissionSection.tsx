import MissionItem from './mission-item/MissionItem.tsx';
import {styled} from "@mui/system";
import {Box, Typography} from "@mui/material";

const missionValues = [
  {
    iconSrc: "/static/images/about_us/quality_icon.jpg",
    title: "Quality",
    description: "We deliver exceptional service using the finest techniques and products."
  },
  {
    iconSrc: "/static/images/about_us/integrity_icon.jpg",
    title: "Integrity",
    description: "We operate with honesty, transparency, and a deep respect for our clients."
  },
  {
    iconSrc: "/static/images/about_us/innovation_icon.jpeg",
    title: "Innovation",
    description: "We continually evolve to bring the latest trends and innovations in wellness."
  }
];

const SecondaryWrapper = styled(Box)(({theme}) => ({
  textAlign: 'center',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  flexDirection: 'column',
  gap: theme.spacing(4),
  padding: theme.spacing(2),
  marginTop: theme.spacing(4),
  marginBottom: theme.spacing(4),
}));

const MissionSection = () => {
  return (
    <Box p={2} pl={4} pr={4} width={'100%'} sx={{backgroundColor: 'rgba(0, 0, 0, 0.05)'}}>
      <SecondaryWrapper>
        <Typography variant={"h2"} gutterBottom>
        Our Mission and Values
        </Typography>
        <Typography variant={"h5"} paragraph>
        At Flow, our mission is to enhance the well-being of every individual we serve through personalized wellness
        solutions. We are committed to creating an environment that fosters health, relaxation, and a sense of
        community.
        </Typography>
        <Box display={'flex'} justifyContent={'center'}
             flexWrap={'wrap'} gap={5} mt={4}>
          {missionValues.map((value, index) => (
            <MissionItem
              key={index}
              iconSrc={value.iconSrc}
              title={value.title}
              description={value.description}
            />
          ))}
        </Box>
      </SecondaryWrapper>
    </Box>
  );
};

export default MissionSection;