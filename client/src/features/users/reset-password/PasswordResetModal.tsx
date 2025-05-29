import {Box, Button, Modal, Stack, TextField, Typography} from '@mui/material';
import {SubmitHandler, useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {passwordValidation} from "../../../shared/validation/users.validations.ts";
import useResetPasswordMutationWithAuth from "../../../hooks/users/mutations/useResetPasswordMutationWithAuth.ts";
import {useConfirmationModal} from "../../../shared/context/ConfirmationModalContext.tsx";
import {UserPasswordUpdate} from "../../../shared/models/api/auth.ts";
import {UserAuthContext} from "../../../shared/context/UserAuthContext.tsx";
import {useContext} from "react";
import {UserRole} from "../../../shared/models/user.types.ts";
import {useNavigate} from "react-router-dom";

interface PasswordResetModalProps {
  open: boolean;
  onClose: () => void;
}

interface PasswordResetInputs {
  newPassword: string;
  confirmPassword: string;
}

const passwordSchema = z.object({
  newPassword: passwordValidation,
  confirmPassword: passwordValidation,
}).refine(data => data.newPassword === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
});

const PasswordResetModal = ({open, onClose}: PasswordResetModalProps) => {
  const {logout, role} = useContext(UserAuthContext)!;

  const navigate = useNavigate();
  const {register, handleSubmit, formState: {errors}} = useForm<PasswordResetInputs>({
    resolver: zodResolver(passwordSchema),
  });
  const resetPasswordMutation = useResetPasswordMutationWithAuth();
  const {openModal, closeModal} = useConfirmationModal();

  const loginRedirect = role === UserRole.CLIENT ? '/login' : '/secret-login';

  const onSubmit: SubmitHandler<PasswordResetInputs> = (data) => {
    const body: UserPasswordUpdate = {
      newPassword: data.newPassword,
    };
    const onConfirm = () => {
      resetPasswordMutation.mutate(body, {
        onSettled: () => closeModal(),
        onSuccess: () => {
          onClose();
          logout();
          navigate(loginRedirect);
        },
      });
    }

    openModal("Reset Password", "Are you sure you want to reset your password? You will need to log in again afterward.", onConfirm);
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      sx={{
        minWidth: 300,
        overflowX: 'auto',
        margin: 2,
      }}
    >
      <Box
        position="absolute"
        top="50%"
        left="50%"
        sx={{
          transform: 'translate(-50%, -50%)',
          backgroundColor: 'background.paper',
          p: 4,
          borderRadius: 2,
          boxShadow: 24,
          minWidth: '50%',
        }}
      >
        <Typography variant="h6" component="h2" gutterBottom>
          Reset Password
        </Typography>
        <form onSubmit={handleSubmit(onSubmit)}>
          <Stack spacing={2}>
            <TextField
              label="New Password"
              type="password"
              {...register('newPassword')}
              error={!!errors.newPassword}
              helperText={errors.newPassword ? errors.newPassword.message : ''}
            />
            <TextField
              label="Confirm Password"
              type="password"
              {...register('confirmPassword')}
              error={!!errors.confirmPassword}
              helperText={errors.confirmPassword ? errors.confirmPassword.message : ''}
            />
            <Button variant="contained" color="primary" type="submit">
              Reset Password
            </Button>
          </Stack>
        </form>
      </Box>
    </Modal>
  );
};

export default PasswordResetModal;