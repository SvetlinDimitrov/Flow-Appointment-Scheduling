import {
  Box,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  FormControlLabel,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  TextField
} from "@mui/material";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {ServiceDTO} from "../../../shared/models/api/services.ts";
import {serviceCreateUpdateValidations} from "../../../shared/validation/services.validations.ts";
import useGetAllWorkSpacesNamesQuery from "../../../hooks/services/query/useGetAllWorkSpacesNamesQuery.ts";
import LoadingSpinner from "../../../shared/core/loading/main-loader/LoadingSpinner.tsx";
import useCreateServiceMutation from "../../../hooks/services/mutations/useCreateServiceMutation.ts";
import ErrorPage from "../../../shared/core/error-page/ErrorPage.tsx";
import {useConfirmationModal} from "../../../shared/context/ConfirmationModalContext.tsx";

interface CreateServiceModalProps {
  open: boolean;
  onClose: () => void;
}

const serviceSchema = serviceCreateUpdateValidations;

const CreateServiceModal = ({open, onClose}: CreateServiceModalProps) => {
  const { register, handleSubmit, formState: { errors }, setValue , reset } = useForm<ServiceDTO>({
    resolver: zodResolver(serviceSchema),
  });
  const {data, isLoading, error} = useGetAllWorkSpacesNamesQuery();
  const createServiceMutation = useCreateServiceMutation();
  const {openModal, closeModal} = useConfirmationModal();

  const onSubmitForm = (data: ServiceDTO) => {
    data.duration *= 60;

    const onConfirm = () => {
      createServiceMutation.mutate({service: data}, {
        onSettled: () => {
          closeModal();
        },
        onSuccess: () => {
          handleClose();
          reset();
        }
      });
    }

    openModal("Create Service", `Are you sure you want to create the service: ${data.name}?`, onConfirm);
  };

  const handleClose = () => {
    onClose();
    reset();
  };

  if (isLoading) return <LoadingSpinner/>;
  if (error) return <ErrorPage/>;

  if (!data) return null;

  return (
    <Dialog open={open} onClose={handleClose} sx={{
      '& .MuiDialog-paper': {
        width: '100%',
        maxWidth: 500,
        minWidth: 300,
      }
    }}>
      <DialogTitle>Create New Service</DialogTitle>
      <form onSubmit={handleSubmit(onSubmitForm)}>
        <DialogContent>
          <Box display={'flex'}
               flexDirection={'column'}
               gap={3}
               p={2}
          >
            <TextField
              label="Name"
              {...register("name")}
              error={!!errors.name}
              helperText={errors.name?.message}
              fullWidth
              size="small"
            />
            <TextField
              label="Description"
              {...register("description")}
              error={!!errors.description}
              helperText={errors.description?.message}
              fullWidth
              size="small"
            />
            <TextField
              label="Duration"
              type="number"
              {...register("duration", {valueAsNumber: true})}
              error={!!errors.duration}
              helperText={errors.duration ?
                errors.duration.message : "Please enter the duration in minutes"
              }
              fullWidth
              size="small"
            />
            <TextField
              label="Price"
              type="number"
              {...register("price", {valueAsNumber: true})}
              error={!!errors.price}
              helperText={errors.price?.message}
              fullWidth
              size="small"
            />
            <FormControl error={!!errors.workSpaceName} fullWidth size="small">
              <InputLabel id="workspace-label">Workspace Name</InputLabel>
              <Select
                labelId="workspace-label"
                label="Workspace Name"
                {...register("workSpaceName")}
                onChange={(e) => setValue("workSpaceName", e.target.value as string)}
                size="small"
                defaultValue={data[0]}
              >
                {data.map((service) => (
                  <MenuItem key={service} value={service}>
                    {service}
                  </MenuItem>
                ))}
              </Select>
              {errors.workSpaceName && <FormHelperText>{errors.workSpaceName.message}</FormHelperText>}
            </FormControl>
            <FormControlLabel
              control={<Checkbox {...register("availability")} size="small"/>}
              label="Availability"
            />

          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} color="primary" size="small">
            Cancel
          </Button>
          <Button
            type="submit"
            color="primary"
            variant="contained"
            size="small"
          >
            Create
          </Button>

        </DialogActions>
      </form>
    </Dialog>
  );
};

export default CreateServiceModal;