import IntroductionSection from "./introduction-section/IntroductionSection.tsx";
import MissionSection from "./mission-section/MissionSection.tsx";
import MeetTeamSection from "./meet-team-section/MeetTeamSection.tsx";
import {styled} from "@mui/system";
import {Box} from "@mui/material";

const MainWrapper = styled(Box)(({theme}) => ({
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  gap: theme.spacing(4),
  marginTop: theme.spacing(5),
  marginBottom: theme.spacing(5),
}));

const AboutUs = () => {
  return (
    <MainWrapper>
      <IntroductionSection/>
      <MissionSection/>
      <MeetTeamSection/>
    </MainWrapper>
  );
};

export default AboutUs;