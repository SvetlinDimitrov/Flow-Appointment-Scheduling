import {SubmitHandler, useForm} from 'react-hook-form';
import {Button, TextField, Typography} from '@mui/material';
import useCreateUserMutation from "../../../hooks/users/mutations/useCreateUserMutation.ts";
import {Link, useNavigate} from "react-router-dom";
import {CreateUserRequest} from "../../../shared/models/api/users.ts";
import {emailValidation, nameValidation, passwordValidation} from "../../../shared/validation/users.validations.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {UserMainWrapper, UserSecondWrapper} from "../../../shared/styles/wrappers.ts";
import useProcessing from "../../../hooks/custom/useProcessing.ts";
import FullScreenLoader from "../../../shared/core/loading/full-screen-loader/FullScreenLoader.tsx";

interface IFormInput {
  email: string;
  firstName: string;
  lastName: string;
  password: string;
  confirmPassword: string;
}

const Register = () => {

  // @ts-ignore
  const {register, handleSubmit, watch, formState: {errors}} = useForm<IFormInput>({
    resolver: zodResolver(
      z.object({
        email: emailValidation,
        firstName: nameValidation,
        lastName: nameValidation,
        password: passwordValidation,
        confirmPassword: z.string().refine(value => value === watch('password'), 'Passwords do not match')
      }))
  });

  const createUserMutation = useCreateUserMutation();
  const {processing, startProcessing, stopProcessing} = useProcessing();

  const navigate = useNavigate();

  const onSubmit: SubmitHandler<IFormInput> = data => {
    const body: CreateUserRequest = {
      email: data.email,
      firstName: data.firstName,
      lastName: data.lastName,
      password: data.password
    };

    startProcessing();

    createUserMutation.mutate(body, {
      onSuccess: () => navigate('/login'),
      onSettled: () => stopProcessing()
    });
  };

  return (
    <>
      <FullScreenLoader isLoading={processing}/>
      <UserMainWrapper>
        <UserSecondWrapper elevation={4}>
          <Typography variant="h4" component="h1" gutterBottom>
            Register
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
              label="First Name"
              type="text"
              fullWidth
              margin="normal"
              {...register('firstName')}
              error={!!errors.firstName}
              helperText={errors.firstName ? errors.firstName.message : ''}
            />
            <TextField
              label="Last Name"
              type="text"
              fullWidth
              margin="normal"
              {...register('lastName')}
              error={!!errors.lastName}
              helperText={errors.lastName ? errors.lastName.message : ''}
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
            <TextField
              label="Confirm Password"
              type="password"
              fullWidth
              margin="normal"
              {...register('confirmPassword')}
              error={!!errors.confirmPassword}
              helperText={errors.confirmPassword ? errors.confirmPassword.message : ''}
            />
            <Button variant="contained" color="primary"
                    sx={{mt: 2, mb: 1, width: '100%'}}
                    type="submit">
              Register
            </Button>
            <Typography
              variant="body2"
              align="center"
              mt={2}
              lineHeight={1.5}
            >
              Already have an account? <Link to="/login">Login here</Link>
            </Typography>
          </form>
        </UserSecondWrapper>
      </UserMainWrapper>
    </>
  );
};

export default Register;