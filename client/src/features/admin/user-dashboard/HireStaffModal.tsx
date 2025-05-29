import {Box, Button, Checkbox, FormControlLabel, MenuItem, Modal, TextField, Typography} from '@mui/material';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {UserRole} from "../../../shared/models/user.types.ts";
import {HireStaffRequest} from "../../../shared/models/api/users.ts";
import {
  nameValidation,
  passwordValidation,
  staffDetailsCreateUpdateValidation
} from "../../../shared/validation/users.validations.ts";
import useHireStaffMutation from "../../../hooks/users/mutations/useHireStuffMutation.ts";
import {useConfirmationModal} from '../../../shared/context/ConfirmationModalContext.tsx';

interface HireStaffForm {
  userInfo: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    confirmPassword: string;
  };
  staffDetailsDto: {
    userRole: UserRole;
    salary: number;
    beginWorkingHour: string;
    endWorkingHour: string;
    isAvailable: boolean;
  };
}

interface HireStaffModalProps {
  open: boolean;
  onClose: () => void;
}

const emailValidation = z.string()
  .min(4, {message: "Email must be at least 4 characters long"})
  .refine(email => !email.includes('@'), {
    message: "Only the first part of the email should be written, without any domain"
  });

const schema = z.object({
  userInfo: z.object({
    firstName: nameValidation,
    lastName: nameValidation,
    email: emailValidation,
    password: passwordValidation,
    confirmPassword: z.string(),
  }).refine(data => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  }),
  staffDetailsDto: staffDetailsCreateUpdateValidation
});

const HireStaffModal = ({open, onClose}: HireStaffModalProps) => {
  const {register, handleSubmit, formState: {errors}, reset} = useForm<HireStaffForm>({
    resolver: zodResolver(schema),
    defaultValues: {
      userInfo: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      staffDetailsDto: {
        userRole: UserRole.EMPLOYEE,
        salary: 0,
        beginWorkingHour: '',
        endWorkingHour: '',
        isAvailable: true
      },
    },
  });
  const hireStaffMutation = useHireStaffMutation();
  const {openModal, closeModal} = useConfirmationModal();

  const onCloseModal = () => {
    reset();
    onClose();
  }

  const onSubmitForm = (data: HireStaffForm) => {
    let email = data.userInfo.email;
    if (!email.endsWith('@flow.com')) {
      email = `${email}@flow.com`;
    }

    const hireStaffRequest: HireStaffRequest = {
      userInfo: {
        ...data.userInfo,
        email
      },
      staffDetailsDto: data.staffDetailsDto
    };

    const onConfirm = () => {
      hireStaffMutation.mutate(hireStaffRequest, {
        onSettled: () => closeModal(),
        onSuccess: () => onCloseModal()
      });
    };
    openModal("Hire Staff", `Are you sure you want to hire the user: ${hireStaffRequest.userInfo.email}?`, onConfirm);
  };

  return (
    <Modal
      open={open}
      onClose={onCloseModal}
      style={{display: 'flex', alignItems: 'center', justifyContent: 'center'}}
    >
      <Box
        p={4}
        bgcolor="background.paper"
        borderRadius={2}
        minWidth={300}
        height={600}
        maxWidth={500}
        overflow={'auto'}
      >
        <Typography variant="h5" mb={2}>Hire Staff Member</Typography>
        <form onSubmit={handleSubmit(onSubmitForm)}>
          <TextField
            size={'small'}
            label="First Name"
            {...register("userInfo.firstName")}
            fullWidth
            margin="normal"
            error={!!errors.userInfo?.firstName}
            helperText={errors.userInfo?.firstName?.message}
          />
          <TextField
            size={'small'}
            label="Last Name"
            {...register("userInfo.lastName")}
            fullWidth
            margin="normal"
            error={!!errors.userInfo?.lastName}
            helperText={errors.userInfo?.lastName?.message}
          />
          <TextField
            size={'small'}
            label="Email"
            {...register("userInfo.email")}
            fullWidth
            margin="normal"
            error={!!errors.userInfo?.email}
            helperText={errors.userInfo?.email?.message}
          />
          <TextField
            size={'small'}
            label="Password"
            type="password"
            {...register("userInfo.password")}
            fullWidth
            margin="normal"
            error={!!errors.userInfo?.password}
            helperText={errors.userInfo?.password?.message}
          />
          <TextField
            size={'small'}
            label="Confirm Password"
            type="password"
            {...register("userInfo.confirmPassword")}
            fullWidth
            margin="normal"
            error={!!errors.userInfo?.confirmPassword}
            helperText={errors.userInfo?.confirmPassword?.message}
          />
          <TextField
            size={'small'}
            select
            label="User Role"
            {...register("staffDetailsDto.userRole")}
            fullWidth
            margin="normal"
            error={!!errors.staffDetailsDto?.userRole}
            helperText={errors.staffDetailsDto?.userRole?.message}
            defaultValue={UserRole.EMPLOYEE}
          >
            <MenuItem value={UserRole.ADMINISTRATOR}>Administrator</MenuItem>
            <MenuItem value={UserRole.EMPLOYEE}>Employee</MenuItem>
          </TextField>
          <TextField
            size={'small'}
            label="Salary"
            type="number"
            {...register("staffDetailsDto.salary")}
            fullWidth
            margin="normal"
            error={!!errors.staffDetailsDto?.salary}
            helperText={errors.staffDetailsDto?.salary?.message}
          />
          <TextField
            size={'small'}
            label="Begin Working Hour"
            type="time"
            {...register("staffDetailsDto.beginWorkingHour")}
            fullWidth
            margin="normal"
            error={!!errors.staffDetailsDto?.beginWorkingHour}
            helperText={errors.staffDetailsDto?.beginWorkingHour?.message}
            InputLabelProps={{shrink: true}}
          />
          <TextField
            size={'small'}
            label="End Working Hour"
            type="time"
            {...register("staffDetailsDto.endWorkingHour")}
            fullWidth
            margin="normal"
            error={!!errors.staffDetailsDto?.endWorkingHour}
            helperText={errors.staffDetailsDto?.endWorkingHour?.message}
            InputLabelProps={{shrink: true}}
          />
          <FormControlLabel
            control={
              <Checkbox
                {...register("staffDetailsDto.isAvailable")}
                defaultChecked={true}
              />
            }
            label="Is Available"
          />
          <Button variant="contained" color="primary" type="submit" fullWidth>
            Submit
          </Button>
        </form>
      </Box>
    </Modal>
  );
};

export default HireStaffModal;