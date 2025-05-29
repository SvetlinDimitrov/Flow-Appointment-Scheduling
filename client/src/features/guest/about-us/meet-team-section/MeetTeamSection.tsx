import TeamMember from "./team-member/TeamMember.tsx";
import {styled} from "@mui/system";
import {Box, Button, Typography} from "@mui/material";

const teamMembers = [
  {
    name: "Jane Doe",
    role: "Lead Massage Therapist",
    description: "With over 10 years of experience, Jane specializes in deep tissue and sports massage, helping clients achieve optimal muscle recovery and relaxation.",
    imageSrc: "/static/images/about_us/male_user_icon.webp"
  },
  {
    name: "John Smith",
    role: "Fitness Instructor",
    description: "John is a certified fitness instructor with a passion for helping clients achieve their fitness goals through personalized training programs.",
    imageSrc: "/static/images/about_us/male_user_icon.webp"
  },
  {
    name: "Emily Johnson",
    role: "Yoga Instructor",
    description: "Emily is a dedicated yoga instructor who specializes in Vinyasa and Hatha yoga, helping clients find balance and peace through mindful practice.",
    imageSrc: "/static/images/about_us/female_user_icon.png"
  }
];

const MainWrapper = styled(Box)(({theme}) => ({
  textAlign: 'center',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  flexDirection: 'column',
  gap: theme.spacing(4),
  padding: theme.spacing(2),
  paddingLeft: theme.spacing(4),
  paddingRight: theme.spacing(4),
}));

const MeetTeamSection = () => {
  return (
    <MainWrapper>
      <Typography variant={"h2"}>
        Meet Our Expert Team
      </Typography>
      <Typography variant={"h5"} paragraph>
        Our team is a group of passionate professionals dedicated to your wellness. From our skilled massage therapists
        to our energetic fitness instructors, every member of Flow brings a wealth of experience and a commitment to
        your well-being.
      </Typography>
      <Box display={'flex'} justifyContent={'center'}
           flexWrap={'wrap'} gap={4} mt={4}>
        {teamMembers.map((member, index) => (
          <TeamMember
            key={index}
            name={member.name}
            role={member.role}
            description={member.description}
            imageSrc={member.imageSrc}
          />
        ))}
      </Box>
      <Button variant={"contained"} color={"primary"} sx={{marginTop: 4}}>
        Join Our Team
      </Button>
    </MainWrapper>
  );
};

export default MeetTeamSection;