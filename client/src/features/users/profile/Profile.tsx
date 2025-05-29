import {Box, Button, Stack, TextField, Typography, useMediaQuery, useTheme} from '@mui/material';
import {useContext, useState} from 'react';
import {SubmitHandler, useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import useGetUserQuery from '../../../hooks/users/query/useGetUserQuery.ts';
import {UserAuthContext} from '../../../shared/context/UserAuthContext.tsx';
import LoadingSpinner from '../../../shared/core/loading/main-loader/LoadingSpinner.tsx';
import useUpdateUserMutation from '../../../hooks/users/mutations/useUpdateUserMutation.ts';
import useLogoutDeleteUserMutation from '../../../hooks/users/mutations/useLogoutDeleteUserMutation.ts';
import {styled} from '@mui/system';
import ConfirmationModalWrapper from '../../../shared/core/confirm-model/ConfirmationModalWrapper.tsx';
import {useConfirmationModal} from '../../../shared/context/ConfirmationModalContext.tsx';
import {nameValidation} from '../../../shared/validation/users.validations.ts';
import StaffDetailsAccordion from './StaffDetailsAccordion.tsx';
import SaveIcon from '@mui/icons-material/Save';
import DeleteIcon from '@mui/icons-material/Delete';
import LockResetIcon from '@mui/icons-material/LockReset';
import PasswordResetModal from "../reset-password/PasswordResetModal.tsx";
import ErrorPage from "../../../shared/core/error-page/ErrorPage.tsx";

const StyleButton = styled(Button)(() => ({
  flexGrow: 1,
}));

interface FormInputs {
  firstName: string;
  lastName: string;
}

const Profile = () => {
  const [isPasswordResetModalOpen, setPasswordResetModalOpen] = useState(false);

  const {userId} = useContext(UserAuthContext);

  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));
  const {openModal, closeModal} = useConfirmationModal();
  const updateUserMutation = useUpdateUserMutation();
  const {data: user, error, isLoading} = useGetUserQuery(userId);
  const logoutDeleteUserMutation = useLogoutDeleteUserMutation();
  const {register, handleSubmit, formState: {errors, isDirty}, reset} = useForm<FormInputs>({
    defaultValues: {
      firstName: user?.firstName,
      lastName: user?.lastName,
    },
    resolver: zodResolver(
      z.object({
        firstName: nameValidation,
        lastName: nameValidation,
      })
    ),
  });

  const handleUpdateUser: SubmitHandler<FormInputs> = (data) => {
    const onConfirm = () => {
      updateUserMutation.mutate(
        {id: userId, user: {firstName: data.firstName, lastName: data.lastName}}, {
          onSettled: () => closeModal(),
          onSuccess: () => {
            reset({firstName: data.firstName, lastName: data.lastName});
          }
        }
      );
    }

    openModal("Update User", "Are you sure you want to save these changes?", onConfirm);
  };

  const handleDeleteUser = () => {
    const onConfirm = () => {
      logoutDeleteUserMutation.mutate(userId, {
        onSettled: () => closeModal()
      })
    };

    openModal("Delete User", "Are you sure you want to delete this profile?", onConfirm);
  };

  if (isLoading) return <LoadingSpinner/>;
  if (error) return <ErrorPage/>;

  if (!user) return null;

  return (
    <Box width={'80%'} margin={'auto'} mt={6} mb={6}>
      <Typography variant="h4" component="h1" gutterBottom>
        Profile
      </Typography>
      <form onSubmit={handleSubmit(handleUpdateUser)}>
        <Stack spacing={2}>
          <TextField
            label="First Name"
            defaultValue={user.firstName}
            {...register('firstName')}
            error={!!errors.firstName}
            helperText={errors.firstName ? errors.firstName.message : ''}
          />
          <TextField
            label="Last Name"
            defaultValue={user.lastName}
            {...register('lastName')}
            error={!!errors.lastName}
            helperText={errors.lastName ? errors.lastName.message : ''}
          />
          <TextField
            label="Email"
            defaultValue={user.email}
            disabled
          />
          {user.staffDetails && <StaffDetailsAccordion user={user}/>}
          <Box display={'flex'} flexDirection={isSmallScreen ? 'column' : 'row'} gap={2}>
            <StyleButton
              variant="contained"
              color="primary"
              type="submit"
              startIcon={<SaveIcon/>}
              sx={{backgroundColor: theme.palette.success.main}}
              disabled={!isDirty}
            >
              Save
            </StyleButton>
            <StyleButton
              variant="contained"
              onClick={handleDeleteUser}
              startIcon={<DeleteIcon/>}
              sx={{backgroundColor: theme.palette.error.main}}
            >
              Delete
            </StyleButton>
            <StyleButton
              variant="contained"
              onClick={() => setPasswordResetModalOpen(true)}
              startIcon={<LockResetIcon/>}
              sx={{backgroundColor: theme.palette.warning.main}}
            >
              Reset Password
            </StyleButton>
          </Box>
        </Stack>
      </form>
      <PasswordResetModal
        open={isPasswordResetModalOpen}
        onClose={() => setPasswordResetModalOpen(false)}
      />
      <ConfirmationModalWrapper/>
    </Box>
  );
};

export default Profile;