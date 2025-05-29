import { Button, CardActions } from "@mui/material";

interface BookButtonProps {
  bookWithStaff: () => void;
  employeeFirstName: string;
  available: boolean;
}

const UserCardActions = ({ bookWithStaff, employeeFirstName, available }: BookButtonProps) => {
  return (
    <CardActions sx={{ justifyContent: 'center' }}>
      <Button
        size={"small"}
        onClick={bookWithStaff}
        disabled={!available}
        sx={{
          width: '200px',
          display: 'block',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap'
        }}
      >
        Book with {employeeFirstName}
      </Button>
    </CardActions>
  );
};

export default UserCardActions;