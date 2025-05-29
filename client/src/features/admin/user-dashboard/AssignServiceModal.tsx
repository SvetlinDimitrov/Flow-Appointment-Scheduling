import {Box, Button, Modal, Pagination, Typography} from '@mui/material';
import {Service} from '../../../shared/models/service.types.ts';
import usePaginatedQuery from '../../../hooks/custom/usePaginatedQuery.ts';
import useGetAllServicesQuery from '../../../hooks/services/query/useGetAllServicesQuery.ts';
import LoadingSpinner from "../../../shared/core/loading/main-loader/LoadingSpinner.tsx";
import useAssignStaffToServiceMutation from "../../../hooks/services/mutations/useAssignStaffToServiceMutation.ts";
import useGetUserQuery from "../../../hooks/users/query/useGetUserQuery.ts";
import ErrorPage from "../../../shared/core/error-page/ErrorPage.tsx";
import {useConfirmationModal} from "../../../shared/context/ConfirmationModalContext.tsx";

interface AssignServiceModalProps {
  userId: number;
  open: boolean;
  onClose: () => void;
}

const AssignServiceModal = ({userId, open, onClose}: AssignServiceModalProps) => {
  const servicesPerPage = 2;

  const {
    data: servicesData,
    isLoading: servicesIsLoading,
    error: servicesError,
    page,
    handlePageChange
  } = usePaginatedQuery<Service>(useGetAllServicesQuery, 0, servicesPerPage);
  const {
    data: userData,
    error: userError,
    isLoading: userLoading
  } = useGetUserQuery(userId);
  const assignStaffToServiceMutation = useAssignStaffToServiceMutation();
  const {openModal, closeModal} = useConfirmationModal();

  const handleAssignService = (service: Service) => {
    const onConfirm = () => {
      assignStaffToServiceMutation.mutate({id: service.id, staffEmail: userData?.email}, {
        onSuccess: () => onClose(),
        onSettled: () => closeModal()
      });
    }

    openModal(
      'Assign Service',
      `Are you sure you want to assign ${service.name} to ${userData?.email}?`,
      onConfirm
    );
  };

  if (servicesIsLoading || userLoading) return <LoadingSpinner/>;
  if (servicesError || userError) return <ErrorPage/>;

  if(!userData || !servicesData) return null;

  return (
    <Modal
      open={open}
      onClose={onClose}
      style={{
        padding: '12px',
        minWidth: '300px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
    }}
    >
      <Box
        p={2}
        bgcolor="background.paper"
        borderRadius={2}
        width={400}
        mx="auto"
      >
        <Typography variant="h6" mb={2}>Assign Service</Typography>
        {servicesData.content.map((service) => (
          <Box key={service.id}
               mb={2}
               p={2}
               border="1px solid"
               borderColor="divider"
               borderRadius={2}
               display={'flex'}
               justifyContent={'center'}
               flexDirection={'column'}
               gap={1}
               alignItems={'center'}>
            <Typography textAlign={'center'} fontFamily="Arial, sans-serif">
              {service.name}
            </Typography>
            <Typography textAlign={'center'} fontFamily="Arial, sans-serif">
              {service.workSpace.name}
            </Typography>
            {userData.staffDetails?.serviceIds.includes(service.id) ? (
              <Typography
                textAlign="center"
                fontFamily="Arial, sans-serif"
                fontSize="0.875rem"
                color="textSecondary"
              >
                Already assigned
              </Typography>
            ) : (
              <Button
                sx={{width: '50%'}}
                variant="contained"
                color="primary"
                onClick={() => handleAssignService(service)}
              >
                Assign
              </Button>
            )}
          </Box>
        ))}
        <Box display="flex" justifyContent="center" mt={2}>
          <Pagination
            count={servicesData.totalPages}
            page={page + 1}
            onChange={(_, value) => handlePageChange(value)}
            color="primary"
          />
        </Box>
      </Box>
    </Modal>
  );
};

export default AssignServiceModal;