import {Box} from "@mui/material";
import AdminServiceList from "./AdminServiceList.tsx";
import {useState} from "react";
import {Service} from "../../../shared/models/service.types.ts";
import ServiceEditModal from "./ServiceEditModal.tsx";
import useDeleteServiceMutation from "../../../hooks/services/mutations/useDeleteServiceMutation.ts";
import {useConfirmationModal} from "../../../shared/context/ConfirmationModalContext.tsx";
import WelcomeServiceSection from "./WelcomeServiceSection.tsx";
import CreateServiceModal from "./CreateServiceModal.tsx";
import ConfirmationModalWrapper from "../../../shared/core/confirm-model/ConfirmationModalWrapper.tsx";
import AdminServiceStaffModal from "./AdminServiceStaffModal.tsx";
import ServiceStatsModal from "./service-stats/ServiceStatsModal.tsx";

const AdminServiceDashboard = () => {
  const [selectedServiceId, setSelectedServiceId] = useState<number | null>(null);
  const [isEditModalOpen, setEditModalOpen] = useState(false);
  const [isCreateModalOpen, setCreateModalOpen] = useState(false);
  const [isStatisticsModalOpen, setStatisticsModalOpen] = useState(false);
  const [showStaff, setShowStaff] = useState(false);

  const {openModal, closeModal} = useConfirmationModal();

  const deleteServiceMutation = useDeleteServiceMutation();

  const handleDeleteService = (service: Service) => {
    const onConfirm = () => {
      if (service)
        deleteServiceMutation.mutate(service.id, {
          onSuccess: () => {
            setSelectedServiceId(null)
            closeModal();
          }
        });
    };

    openModal("Delete Service", `Are you sure you want to delete the service: ${service.name}?`, onConfirm);
  };

  return (
    <Box
      display={'flex'}
      flexDirection={'column'}
      justifyContent={'center'}
      width={'80%'}
      margin={'auto'}
      minWidth={300}
      mt={10}
    >
      <WelcomeServiceSection onCreateService={() => setCreateModalOpen(true)}/>
      <AdminServiceList
        handleStatistics={(service) => {
          setSelectedServiceId(service.id);
          setStatisticsModalOpen(true);
        }}
        handleViewStaff={(service) => {
          setSelectedServiceId(service.id);
          setShowStaff(true);
        }}
        handleUpdateService={(service) => {
          setSelectedServiceId(service.id);
          setEditModalOpen(true);
        }}
        handleDeleteService={handleDeleteService}
      />

      {selectedServiceId &&
        <AdminServiceStaffModal
          open={showStaff}
          onClose={() => {
            setShowStaff(false);
            setSelectedServiceId(null);
          }}
          serviceId={selectedServiceId}
        />
      }

      {selectedServiceId && (
        <ServiceEditModal
          open={isEditModalOpen}
          onClose={
            () => {
              setEditModalOpen(false);
              setSelectedServiceId(null);
            }
          }
          id={selectedServiceId}
        />
      )}
      {isCreateModalOpen &&
        <CreateServiceModal
          open={isCreateModalOpen}
          onClose={() => setCreateModalOpen(false)}
        />
      }
      {isStatisticsModalOpen && selectedServiceId &&
        <ServiceStatsModal
          open={isStatisticsModalOpen}
          serviceId={selectedServiceId}
          onClose={() => {
            setStatisticsModalOpen(false);
            setSelectedServiceId(null);
          }}
        />
      }
      <ConfirmationModalWrapper/>
    </Box>
  );
};

export default AdminServiceDashboard;