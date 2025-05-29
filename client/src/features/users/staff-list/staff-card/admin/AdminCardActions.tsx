import {Button, CardActions} from "@mui/material";

interface UserCardActionsProps {
  onDelete: () => void;
}

const AdminCardActions = ({onDelete}: UserCardActionsProps) => {
  return (
    <CardActions sx={{justifyContent: 'center'}}>
      <Button size={"small"} onClick={onDelete}>
        Unassign
      </Button>
    </CardActions>
  );
};

export default AdminCardActions;