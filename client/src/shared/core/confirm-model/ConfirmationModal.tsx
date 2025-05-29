import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, useTheme} from '@mui/material';
import FullScreenLoader from "../loading/full-screen-loader/FullScreenLoader.tsx";

interface ConfirmationModalProps {
  open: boolean;
  isLoading: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}

const ConfirmationModal = (
  {
    open,
    isLoading,
    title,
    message,
    onConfirm,
    onCancel
  }: ConfirmationModalProps) => {
  const theme = useTheme();

  return (
    <Dialog
      open={open}
      onClose={onCancel}
      aria-labelledby="confirmation-dialog-title"
      aria-describedby="confirmation-dialog-description"
      sx={{
        minWidth: 300,
      }}
      PaperProps={{
        sx: {
          padding: theme.spacing(2),
          backgroundColor: theme.palette.background.paper,
          boxShadow: theme.shadows[5],
          borderRadius: theme.shape.borderRadius,
          zIndex: 10000,
        },
      }}
      BackdropProps={{
        sx: {
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
        },
      }}
    >
      <DialogTitle
        id="confirmation-dialog-title"
        sx={{
          color: theme.palette.primary.main,
          minWidth: 300,
          overflowX: 'auto',
        }}>
        {title}
      </DialogTitle>
      <DialogContent >
        <DialogContentText id="confirmation-dialog-description" sx={{color: theme.palette.text.primary}}>
          {message}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancel} color="primary" variant="outlined">
          Cancel
        </Button>
        <Button onClick={onConfirm} color="primary" variant="contained">
          Confirm
        </Button>
      </DialogActions>
      <FullScreenLoader isLoading={isLoading}/>
    </Dialog>
  );
};

export default ConfirmationModal;