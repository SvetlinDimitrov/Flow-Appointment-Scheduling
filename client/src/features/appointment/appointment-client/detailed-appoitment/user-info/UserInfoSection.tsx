import {Card, CardContent, CardMedia, Typography} from '@mui/material';
import {User} from "../../../../../shared/models/user.types.ts";

interface UserInfoSectionProps {
  user: User;
}

const UserInfoSection = ({user}: UserInfoSectionProps) => {
  return (
    <Card sx={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: 2,
      borderRadius: 0,
      boxShadow: 0,
      height: '40%'
    }}>
      <CardMedia
        component="img"
        sx={{width: 80, height: 80, borderRadius: '50%'}}
        image="/static/images/no-picture-found.jpg"
        alt={`${user.firstName} ${user.lastName}`}
      />
      <CardContent>
        <Typography variant="h5">
          {user.firstName} {user.lastName}
        </Typography>
        <Typography variant="subtitle2" fontWeight={'bold'}>
          {user.email}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default UserInfoSection;