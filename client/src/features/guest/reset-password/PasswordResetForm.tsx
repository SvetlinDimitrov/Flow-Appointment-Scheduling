import {Box, Button, Stack, TextField, Typography} from '@mui/material';
import {SubmitHandler, useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {passwordValidation} from "../../../shared/validation/users.validations.ts";
import {UserPasswordUpdate} from "../../../shared/models/api/auth.ts";
import {useNavigate, useSearchParams} from "react-router-dom";
import {isJwtValid} from "../../../utils/jwt/jwtDecoder.ts";
import PageNotFound from "../../../shared/core/not-found/PageNotFound.tsx";
import useResetPasswordMutationWithCustomAuth
  from "../../../hooks/users/mutations/useResetPasswordMutationWithCustomAuth.ts";
import useProcessing from "../../../hooks/custom/useProcessing.ts";
import FullScreenLoader from "../../../shared/core/loading/full-screen-loader/FullScreenLoader.tsx";
import {toast} from "react-toastify";

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

/**
 * This component can be accessed only via an email link. The link will direct to this component with a token parameter.
 * The token will be valid for 15 minutes, and because of this, I am checking its validity in the mutation.
 * If the user manually types this endpoint with an invalid token, they will be redirected to the PageNotFound component.
 * If the token expires after 15 minutes and the link is clicked from the email, the user will again see the PageNotFound component.
 * During this 15-minute period, the user can change their password as many times as they want.
 */
const PasswordResetForm = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  const {register, handleSubmit, formState: {errors}} = useForm<PasswordResetInputs>({
    resolver: zodResolver(passwordSchema),
  });
  const resetPasswordMutation = useResetPasswordMutationWithCustomAuth(token);
  const {processing, startProcessing, stopProcessing} = useProcessing();

  const onSubmit: SubmitHandler<PasswordResetInputs> = (data) => {
    const body: UserPasswordUpdate = {
      newPassword: data.newPassword,
    };

    startProcessing();

    resetPasswordMutation.mutate(body, {
      onSettled: () => stopProcessing(),
      onSuccess: () => navigate('/login'),
      onError: () => toast('Failed to reset password', {type: 'error'}),
    });
  };

  if (!isJwtValid(token)) return <PageNotFound/>;

  return (
    <>
      <FullScreenLoader isLoading={processing}/>
      <Box
        minWidth={300}
        width={'80%'}
        margin={"auto"}
        mt={16}
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
    </>
  );
};

export default PasswordResetForm;