import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {
  Box,
  Button,
  Checkbox,
  FormControl,
  FormControlLabel,
  FormHelperText,
  InputLabel,
  MenuItem,
  Modal,
  Select,
  TextField,
  Typography
} from '@mui/material';
import {serviceCreateUpdateValidations} from "../../../shared/validation/services.validations.ts";
import useGetAllWorkSpacesNamesQuery from "../../../hooks/services/query/useGetAllWorkSpacesNamesQuery.ts";
import LoadingSpinner from "../../../shared/core/loading/main-loader/LoadingSpinner.tsx";
import useUpdateServiceMutation from "../../../hooks/services/mutations/useUpdateServiceMutation.ts";
import useGetServiceByIdQuery from '../../../hooks/services/query/useGetServiceByIdQuery.ts';
import {Duration} from 'luxon';
import ErrorPage from "../../../shared/core/error-page/ErrorPage.tsx";
import {useRef} from "react";
import {useConfirmationModal} from "../../../shared/context/ConfirmationModalContext.tsx";

const serviceSchema = serviceCreateUpdateValidations;

type ServiceFormInputs = z.infer<typeof serviceSchema>;

interface ServiceEditModalProps {
  open: boolean;
  onClose: () => void;
  id: number;
}

const ServiceEditModal = ({open, onClose, id}: ServiceEditModalProps) => {
  const formInitialized = useRef(false);

  const { register, handleSubmit, formState: { errors }, reset } = useForm<ServiceFormInputs>({
    resolver: zodResolver(serviceSchema),
  });

  const updateServiceMutation = useUpdateServiceMutation();
  const {
    data: service,
    isLoading: serviceIsLoading,
    error: serviceError,
    isFetching: serviceIsFetching
  } = useGetServiceByIdQuery(String(id));
  const {
    data: workSpaces,
    isLoading: workSpacesLoading,
    error: workSpacesError
  } = useGetAllWorkSpacesNamesQuery();
  const {openModal, closeModal} = useConfirmationModal();

  if (service && !formInitialized.current && !serviceIsFetching) {
    reset({
      name: service.name,
      description: service.description,
      duration: Duration.fromISO(service.duration).as('minutes'),
      price: service.price,
      availability: service.availability,
      workSpaceName: service.workSpace.name,
    });
    formInitialized.current = true;

  }

  const handleFormSubmit = (data: ServiceFormInputs) => {
    data.duration *= 60;

    const onConfirm = () => {
      updateServiceMutation.mutate({
        serviceId: id,
        service: data,
      }, {
        onSuccess: () => {
          onClose();
          reset();
        },
        onSettled: () => closeModal(),
      });
    }

    openModal("Update Service", `Are you sure you want to update the service: ${data.name}?`, onConfirm);
  };

  const handleClose = () => {
    onClose();
  };

  if (workSpacesLoading || serviceIsLoading) return <LoadingSpinner/>;
  if (workSpacesError || serviceError) return <ErrorPage/>;
  if (!service || !workSpaces) return null;

  return (
    <Modal
      open={open}
      onClose={handleClose}
      aria-labelledby="edit-service-modal"
    >
      <Box
        position={'absolute'}
        top={'50%'}
        left={'50%'}
        maxWidth={500}
        minWidth={300}
        bgcolor={'background.paper'}
        boxShadow={24}
        p={4}
        sx={{
        transform: 'translate(-50%, -50%)',
        }}
      >
        <Typography id="edit-service-modal" variant="h5" component="h2">
          Edit Service
        </Typography>
        <form onSubmit={handleSubmit(handleFormSubmit)}>
          <TextField
            fullWidth
            label="Name"
            {...register('name')}
            error={!!errors.name}
            helperText={errors.name?.message}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Description"
            {...register('description')}
            error={!!errors.description}
            helperText={errors.description?.message}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Duration"
            type="number"
            {...register('duration', { valueAsNumber: true })}
            error={!!errors.duration}
            helperText={errors.duration ? errors.duration.message : "Please enter the duration in minutes"}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Price"
            type="number"
            {...register('price', { valueAsNumber: true })}
            error={!!errors.price}
            helperText={errors.price?.message}
            margin="normal"
          />
          <FormControl error={!!errors.workSpaceName} fullWidth size="small">
            <InputLabel id="workspace-label">Workspace Name</InputLabel>
            <Select
              labelId="workspace-label"
              label="Workspace Name"
              {...register("workSpaceName")}
              size="small"
              defaultValue={service.workSpace.name}
            >
              {workSpaces.map((service) => (
                <MenuItem key={service} value={service}>
                  {service}
                </MenuItem>
              ))}
            </Select>
            {errors.workSpaceName && <FormHelperText>{errors.workSpaceName.message}</FormHelperText>}
          </FormControl>
          <FormControlLabel
            control={
              <Checkbox
                {...register('availability')}
                defaultChecked={service.availability}
              />
            }
            label="Available"
          />
          <Button type="submit" variant="contained" color="primary" fullWidth>
            Save
          </Button>
        </form>
      </Box>
    </Modal>
  );
};

export default ServiceEditModal;
