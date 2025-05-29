import {styled} from "@mui/system";
import {Avatar, Box, Typography} from "@mui/material";

interface TestimonialProps {
  text: string;
  image: string;
  clientName: string;
}

const MainWrapper = styled(Box)(({theme}) => ({
  textAlign: 'center',
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  gap: theme.spacing(2),
  borderRadius: theme.spacing(2),
  padding: theme.spacing(3),
  boxShadow: '0 8px 16px rgba(0, 0, 0, 0.7)',
  backgroundColor: 'rgba(255, 255, 255, 0.1)',
}));

const Testimonial = ({ text, image, clientName }: TestimonialProps) => {
  return (
    <MainWrapper>
      <Typography variant={"h6"} color={'white'}>
    {text}
      </Typography>
      <Box display={'flex'} alignItems={'center'}
           justifyContent={'center'} gap={1}>
        <Avatar alt={clientName} src={image} sx={{
          width: 70,
          height: 70,
          mb: 1,
        }}/>
        <Typography variant="h5" color={'white'}>
          {clientName}
        </Typography>
      </Box>
    </MainWrapper>
  );
};

export default Testimonial;