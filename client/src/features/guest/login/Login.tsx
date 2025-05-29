import { useState } from 'react';
import { SubmitHandler, useForm } from 'react-hook-form';
import { Button, TextField, Typography } from '@mui/material';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { toast } from 'react-toastify';
import useLoginUserMutation from '../../../hooks/users/mutations/useLoginUserMutation.ts';
import { emailValidation, passwordValidation } from '../../../shared/validation/users.validations.ts';
import { UserMainWrapper, UserSecondWrapper } from '../../../shared/styles/wrappers.ts';
import useProcessing from '../../../hooks/custom/useProcessing.ts';
import FullScreenLoader from '../../../shared/core/loading/full-screen-loader/FullScreenLoader.tsx';
import ResetPasswordNoAuthModal from './ResetPasswordNoAuthModal.tsx';

interface IFormInput {
  email: string;
  password: string;
}

const Login = () => {
  const { register, handleSubmit, formState: { errors } } = useForm<IFormInput>({
    resolver: zodResolver(
      z.object({
        email: emailValidation,
        password: passwordValidation,
      }))
  });

  const navigate = useNavigate();
  const location = useLocation();
  const [isModalOpen, setIsModalOpen] = useState(false);

  const userLoginMutation = useLoginUserMutation();
  const { processing, startProcessing, stopProcessing } = useProcessing();

  const onSubmit: SubmitHandler<IFormInput> = data => {
    if (data.email.endsWith('@flow.com') && location.pathname !== '/secret-login') {
      toast.warn('You have been redirected. If you are a company employee, please use the company login page.');
      navigate('/secret-login');
      return;
    } else if (!data.email.endsWith('@flow.com') && location.pathname === '/secret-login') {
      toast.warn('You have been redirected. Please use the regular login page.');
      navigate('/login');
      return;
    }

    const body: IFormInput = {
      email: data.email,
      password: data.password
    };
    startProcessing();
    userLoginMutation.mutate(body, {
      onSuccess: () => navigate('/'),
      onSettled: () => stopProcessing()
    });
  };

  return (
    <>
      <FullScreenLoader isLoading={processing} />
      <UserMainWrapper>
        <UserSecondWrapper elevation={4}>
          <Typography mb={'2'} variant="h4">
            {location.pathname === '/secret-login' ? 'Company Login' : 'Login'}
          </Typography>
          <form onSubmit={handleSubmit(onSubmit)}>
            <TextField
              label="Email"
              type="email"
              fullWidth
              margin="normal"
              {...register('email')}
              error={!!errors.email}
              helperText={errors.email ? errors.email.message : ''}
            />
            <TextField
              label="Password"
              type="password"
              fullWidth
              margin="normal"
              {...register('password')}
              error={!!errors.password}
              helperText={errors.password ? errors.password.message : ''}
            />
            <Button variant="contained" fullWidth
                    sx={{ mt: 2, mb: 1 }}
                    type="submit">
              Login
            </Button>
            <Typography mt={2} textAlign={'center'} variant="body2" align="center">
              Don't have an account? <Link to="/register">Register here</Link>
            </Typography>
            <Typography mt={2} textAlign={'center'} variant="body2" align="center">
              Forgot your password? <Link to="#" onClick={() => setIsModalOpen(true)}>Reset Password</Link>
            </Typography>
          </form>
        </UserSecondWrapper>
      </UserMainWrapper>
      <ResetPasswordNoAuthModal open={isModalOpen} onClose={() => setIsModalOpen(false)} />
    </>
  );
};

export default Login;