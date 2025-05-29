import {Box, Modal, useMediaQuery, useTheme} from "@mui/material";
import StaffList from "../../users/staff-list/StaffList.tsx";
import useUnassignStaffFromServiceMutation
  from "../../../hooks/services/mutations/useUnassignStaffFromServiceMutation.ts";
import {useConfirmationModal} from "../../../shared/context/ConfirmationModalContext.tsx";
import useGetServiceByIdQuery from "../../../hooks/services/query/useGetServiceByIdQuery.ts";
import LoadingSpinner from "../../../shared/core/loading/main-loader/LoadingSpinner.tsx";
import ErrorPage from "../../../shared/core/error-page/ErrorPage.tsx";

interface AdminServiceStaffModalProps {
  open: boolean;
  onClose: () => void;
  serviceId: number;
}

const AdminServiceStaffModal = (
  {
    open,
    onClose,
    serviceId,
  }: AdminServiceStaffModalProps) => {

  const {
    data: service,
    isLoading: serviceIsLoading,
    error: serviceError,
    isFetching: serviceIsFetching
  } = useGetServiceByIdQuery(String(serviceId));
  const unassignStaffFromServiceMutation = useUnassignStaffFromServiceMutation();
  const {openModal, closeModal} = useConfirmationModal();

  const handleUnassignStaffFromService = (staffEmail: string, serviceId: number) => {
    const onConfirm = () => {
      unassignStaffFromServiceMutation.mutate({
        id: serviceId,
        staffEmail: staffEmail,
      }, {
        onSettled: () => closeModal()
      });
    };

    openModal("Unassign Staff", `Are you sure you want to unassign the staff member: ${staffEmail}?`, onConfirm);
  };

  const theme = useTheme();
  const isBelowMd = useMediaQuery(theme.breakpoints.down('md'));
  const isBelowLg = useMediaQuery(theme.breakpoints.down('lg'));
  const isBelowXl = useMediaQuery(theme.breakpoints.down('xl'));

  let showStaffNumbers = 4;
  if (isBelowMd) {
    showStaffNumbers = 1;
  } else if (isBelowLg) {
    showStaffNumbers = 2;
  } else if (isBelowXl) {
    showStaffNumbers = 3;
  }

  if (serviceIsLoading || serviceIsFetching) return <LoadingSpinner/>
  if (serviceError) return <ErrorPage/>

  if (!service) return null;

  return (
    <Modal
      open={open}
      onClose={onClose}
      sx={{
        padding: '10px',
        backdropFilter: 'blur(5px)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minWidth: '300px',
        overflowX: 'auto',
      }}
    >
      <Box
        sx={{
          backgroundColor: 'background.paper',
          boxShadow: 24,
          p: 4,
          borderRadius: 2,
          width: '80%',
          maxHeight: '80%',
          overflowX: 'auto',
        }}
      >
        <StaffList
          selectedService={service}
          handleDeleteEmployeeFromService={handleUnassignStaffFromService}
          showStaffNumbers={showStaffNumbers}
        />
      </Box>
    </Modal>
  );
};

export default AdminServiceStaffModal;