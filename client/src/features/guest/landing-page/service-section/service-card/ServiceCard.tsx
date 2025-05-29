import {Button, Card, CardContent, CardMedia, Typography} from '@mui/material';
import {useNavigate} from 'react-router-dom';

interface ServiceCardProps {
  image: string;
  alt: string;
  title: string;
  navigateTo: string;
  buttonText: string;
  serviceAvailable?: boolean;
}

const ServiceCard = (
  {
    image,
    alt,
    title,
    navigateTo ,
    buttonText ,
    serviceAvailable
  }: ServiceCardProps) => {
  const navigate = useNavigate();

  return (
    <Card sx={{
      width: '100%',
      maxWidth: '450px',
    }}>
      <CardMedia
        component={"img"}
        height={"350"}
        image={image}
        alt={alt}
      />
      <CardContent sx={{
        textAlign: 'center',
      }}>
        <Typography variant={"h5"} gutterBottom noWrap>
          {title}
        </Typography>
        <Button
          variant="outlined"
          onClick={() => navigate(navigateTo)}
          sx={{
            maxWidth: '100%',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap'
          }}
          disabled={!serviceAvailable}
        >
          {buttonText}
        </Button>
      </CardContent>
    </Card>
  );
};

export default ServiceCard;