import {Box, Button, Modal, TextField, Typography} from '@mui/material';
import {SubmitHandler, useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {toast} from "react-toastify";
import {resetPassword} from "../../../services/auth-service.ts";
import useProcessing from "../../../hooks/custom/useProcessing.ts";
import FullScreenLoader from "../../../shared/core/loading/full-screen-loader/FullScreenLoader.tsx";

interface ResetPasswordNoAuthModalProps {
  open: boolean;
  onClose: () => void;
}

const emailValidation = z.string().email("Invalid email address");

const ResetPasswordNoAuthModal = ({open, onClose}: ResetPasswordNoAuthModalProps) => {
  const {register, handleSubmit, formState: {errors}, reset} = useForm<{ email: string }>({
    resolver: zodResolver(z.object({email: emailValidation}))
  });
  const {processing, startProcessing, stopProcessing} = useProcessing();

  const handleResetPassword: SubmitHandler<{ email: string }> = async (data) => {
    try {
      startProcessing();
      await resetPassword(data.email);
      toast.success("Password reset email sent successfully");
      handleClose();
    } catch {
      toast.error("An error occurred while sending the password reset email");
    } finally {
      stopProcessing();
    }
  };

  const handleClose = () => {
    onClose();
    reset();
  }

  return (
    <>
      <FullScreenLoader isLoading={processing}/>
      <Modal
        open={open}
        onClose={handleClose}
        sx={{
          minWidth: 300,
          overflowX: 'auto',
          zIndex: 99
        }}
      >
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          bgcolor="background.paper"
          width={400}
          p={4}
          borderRadius={2}
          boxShadow={24}
          position="absolute"
          sx={{
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
          }}
        >
          <Typography variant="h6" component="h2" gutterBottom>
            Reset Password
          </Typography>
          <form onSubmit={handleSubmit(handleResetPassword)}>
            <TextField
              label="Email"
              type="email"
              fullWidth
              size={'small'}
              margin="normal"
              {...register('email')}
              error={!!errors.email}
              helperText={errors.email ? errors.email.message : ''}
            />
            <Button
              size={'small'}
              variant="contained"
              color="primary"
              type="submit">
              Submit
            </Button>
          </form>
        </Box>
      </Modal>
    </>
  );
};

export default ResetPasswordNoAuthModal;