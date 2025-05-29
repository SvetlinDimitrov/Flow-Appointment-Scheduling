import {
  Box,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  MenuItem,
  TextField
} from '@mui/material';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {CreateUpdateUserAdminRequest} from "../../../shared/models/api/users.ts";
import {UserRole} from "../../../shared/models/user.types.ts";
import {staffDetailsCreateUpdateValidation} from "../../../shared/validation/users.validations.ts";
import useModifyStaffMutation from "../../../hooks/users/mutations/useModifyStaffMutation.ts";
import useGetUserQuery from '../../../hooks/users/query/useGetUserQuery.ts';
import LoadingSpinner from "../../../shared/core/loading/main-loader/LoadingSpinner.tsx";
import ErrorPage from "../../../shared/core/error-page/ErrorPage.tsx";
import {DateTime} from "luxon";
import {useRef} from "react";
import {useConfirmationModal} from "../../../shared/context/ConfirmationModalContext.tsx";

interface EditUserModalProps {
  open: boolean;
  onClose: () => void;
  userId: number;
}

const formatDateTime = (dateTime: string | undefined) => {
  return dateTime ? DateTime.fromISO(dateTime).toFormat('HH:mm') : undefined;
};

const EditUserModal = ({open, onClose, userId}: EditUserModalProps) => {
  const formInitialized = useRef(false);

  const {
    data: userData,
    error: userError,
    isLoading: userLoading,
    isFetching: userFetching,
  } = useGetUserQuery(userId);
  const {
    register,
    handleSubmit,
    reset,
    formState: {errors}
  } = useForm<CreateUpdateUserAdminRequest>({
    resolver: zodResolver(staffDetailsCreateUpdateValidation)
  });
  const modifyStaffMutation = useModifyStaffMutation();
  const {openModal, closeModal} = useConfirmationModal();

  const onSubmit = (data: CreateUpdateUserAdminRequest) => {
    const onConfirm = () => {
      modifyStaffMutation.mutate({id: userId, modifyDto: data}, {
        onSettled: () => {
          onClose();
          closeModal();
        }
      });
    };

    openModal("Edit User", `Are you sure you want to edit the user: ${userData?.email}?`, onConfirm);
  };

  /**
   * Resets the form with user data when available and not already initialized.
   *
   * This block checks if `userData` is available, the form has not been initialized,
   * and the data is not currently being fetched. If all conditions are met, it resets
   * the form with the user data and marks the form as initialized.
   *
   * @param {Object} userData - The user data fetched from the server.
   * @param {boolean} userFetching - Indicates if the user data is currently being fetched.
   * @param {Function} reset - The function to reset the form values.
   * @param {Object} formInitialized - A ref object to track if the form has been initialized.
   */
  if (userData && !formInitialized.current && !userFetching) {
    reset({
      isAvailable: userData.staffDetails?.isAvailable,
      salary: userData.staffDetails?.salary,
      beginWorkingHour: formatDateTime(userData.staffDetails?.beginWorkingHour?.toString()),
      endWorkingHour: formatDateTime(userData.staffDetails?.endWorkingHour?.toString()),
    });
    formInitialized.current = true;
  }

  if (userLoading) return <LoadingSpinner/>;
  if (userError) return <ErrorPage/>;
  if (!userData || !userData.staffDetails) return null;

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="lg" sx={{
      minWidth: 300,
    }}>
      <Box minWidth={300}>
      <DialogTitle>Edit User</DialogTitle>
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogContent>
            <TextField
              select
              label="User Role"
              {...register("userRole")}
              fullWidth
              margin="normal"
              error={!!errors.userRole}
              defaultValue={userData.role}
            >
              <MenuItem value={UserRole.ADMINISTRATOR}>Administrator</MenuItem>
              <MenuItem value={UserRole.EMPLOYEE}>Employee</MenuItem>
            </TextField>
            <TextField
              {...register("salary")}
              label="Salary"
              type="number"
              fullWidth
              margin="normal"
              error={!!errors.salary}
              helperText={errors.salary?.message}
            />
            <TextField
              {...register("beginWorkingHour")}
              label="Begin Working Hour"
              type="time"
              fullWidth
              margin="normal"
              error={!!errors.beginWorkingHour}
              helperText={errors.beginWorkingHour?.message}
              InputLabelProps={{
                shrink: true,
              }}
            />
            <TextField
              {...register("endWorkingHour")}
              label="End Working Hour"
              type="time"
              fullWidth
              margin="normal"
              error={!!errors.endWorkingHour}
              helperText={errors.endWorkingHour?.message}
              InputLabelProps={{
                shrink: true,
              }}
            />
            <FormControlLabel
              control={
                <Checkbox
                  {...register("isAvailable")}
                  defaultChecked={userData.staffDetails.isAvailable}
                />
              }
              label="Is Available"
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={onClose} color="secondary">
              Cancel
            </Button>
            <Button type="submit" color="primary">
              Save
            </Button>
          </DialogActions>
        </form>
      </Box>
    </Dialog>
  );
};

export default EditUserModal;