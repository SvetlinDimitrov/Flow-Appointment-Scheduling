import Testimonial from "./testimonial/Testimonial.tsx";
import {useNavigate} from "react-router-dom";
import {styled} from "@mui/system";
import {Box, Button, Typography} from "@mui/material";

const testimonials = [
  {
    text: '“Flow has transformed my wellness routine – their service are top-notch!”',
    image: '/static/images/home/customer_user_icon.png',
    clientName: 'John Doe',
  },
  {
    text: '“Booking appointment is so easy and convenient with Flow\'s online system.”',
    image: '/static/images/home/customer_user_icon.png',
    clientName: 'Jane Smith',
  },
  {
    text: '“The staff at Flow are incredibly friendly and professional. Highly recommend!”',
    image: '/static/images/home/customer_user_icon.png',
    clientName: 'Alice Johnson',
  },
  {
    text: '“Flow\'s environment is so relaxing and welcoming. I always look forward to my visits.”',
    image: '/static/images/home/customer_user_icon.png',
    clientName: 'Michael Brown',
  },
  {
    text: '“The personalized wellness solutions at Flow have greatly improved my quality of life.”',
    image: '/static/images/home/customer_user_icon.png',
    clientName: 'Emily Davis',
  },
];

const MainWrapper = styled(Box)(({theme}) => ({
  position: 'relative',
  textAlign: 'center',
  padding: theme.spacing(3),
  backgroundImage: 'url("/static/images/home/customer_section_bg.jpg")',
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  height: '100vh',
  paddingLeft: theme.spacing(4),
  paddingRight: theme.spacing(4),
  [theme.breakpoints.down('lg')]: {
    height: 'auto',
  },
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  gap: theme.spacing(6),
  alignItems: 'center',
  '::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    backgroundColor: 'rgba(0, 0, 0, 0.6)',
    zIndex: 1,
  },
  '& > *': {
    position: 'relative',
    zIndex: 2,
  },
}));

const TestimonialWrapper = styled(Box)(({theme}) => ({
  display: 'grid',
  gridTemplateColumns: 'repeat(3, 1fr)',
  [theme.breakpoints.down('lg')]: {
    gridTemplateColumns: 'repeat(2, 1fr)',
  },
  [theme.breakpoints.down('sm')]: {
    gridTemplateColumns: 'repeat(1, 1fr)',
  },
  gap: theme.spacing(4),
  justifyContent: 'center',
  alignItems: 'center',
}));

const CustomerTestimonialsSection = () => {
  const navigate = useNavigate();

  return (
    <MainWrapper>
      <Typography color={'white'} mb={4} variant={"h2"}>
        Why Choose Flow?
      </Typography>
      <TestimonialWrapper>
        {testimonials.map((testimonial, index) => (
          <Testimonial key={index}
                       text={testimonial.text}
                       image={testimonial.image}
                       clientName={testimonial.clientName}/>
        ))}
      </TestimonialWrapper>
      <Button variant={"contained"} color={"primary"}
              sx={{
                mt: 2, pl: 2, pr: 2,
                pt: 1, pb: 1, fontSize: '1rem',
              }}
              onClick={() => navigate('/about-us')}>
        About Us
      </Button>
    </MainWrapper>
  );
};

export default CustomerTestimonialsSection;