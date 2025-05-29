import {Grid, Button} from "@mui/material";

interface AdminButtonsProps {
  handleUpdateService: () => void;
  handleDeleteService: () => void;
}

const AdminButtons = ({handleUpdateService, handleDeleteService}: AdminButtonsProps) => (
  <Grid container spacing={2} justifyContent="center">
    <Grid item>
      <Button variant="contained" color="primary" onClick={handleUpdateService}>Edit</Button>
    </Grid>
    <Grid item>
      <Button variant="contained" color="error" onClick={handleDeleteService}>Delete</Button>
    </Grid>
  </Grid>
);

export default AdminButtons;