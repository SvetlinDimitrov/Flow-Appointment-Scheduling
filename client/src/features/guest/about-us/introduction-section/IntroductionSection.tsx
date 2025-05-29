import {styled} from "@mui/system";
import {Box, Typography} from "@mui/material";

const Image = styled('img')(({theme}) => ({
  width: '100%',
  maxWidth: 600,
  borderRadius: theme.shape.borderRadius,
  marginTop: theme.spacing(2),
}));

const IntroductionSection = () => {
  return (
    <Box textAlign={'center'} p={2} pl={4} pr={4}>
      <Typography variant={"h2"} gutterBottom>
        Our Journey to Wellness
      </Typography>
      <Typography variant={"h5"} paragraph>
        Flow was born out of a passion for wellness and a desire to create a sanctuary where people can rejuvenate their
        bodies and minds. Since our inception, we've been dedicated to offering top-tier fitness, spa, and wellness
        services tailored to the needs of our diverse clientele.
      </Typography>
      <Image
        src="/static/images/about_us/introduction_section_group.jpg"
        alt="Welcoming image of the spa"
      />
    </Box>
  );
};

export default IntroductionSection;