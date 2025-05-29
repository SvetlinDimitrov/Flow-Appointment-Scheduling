import {Box, IconButton} from '@mui/material';
import {DataGrid, GridColDef} from '@mui/x-data-grid';
import {useState} from 'react';
import {UserRole} from '../../../shared/models/user.types.ts';
import {useConfirmationModal} from "../../../shared/context/ConfirmationModalContext.tsx";
import useDeleteUserMutation from "../../../hooks/users/mutations/useDeleteUserMutation.ts";
import ConfirmationModalWrapper from "../../../shared/core/confirm-model/ConfirmationModalWrapper.tsx";
import EditUserModal from "./EditUserModal.tsx";
import AssignServiceModal from "./AssignServiceModal.tsx";
import AdminCalendarModal from "../../appointment/appoitment-admin/AdminCalendarModal.tsx";
import LoadingSpinner from "../../../shared/core/loading/main-loader/LoadingSpinner.tsx";
import ErrorPage from "../../../shared/core/error-page/ErrorPage.tsx";
import useGetUsers from "../../../hooks/users/query/useGetUsers.ts";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AssignmentIcon from '@mui/icons-material/Assignment';
import EventIcon from '@mui/icons-material/Event';
import HireStaffModal from "./HireStaffModal.tsx";
import AdminDashboardHeader from "./AdminDashboardHeader.tsx";
import {FetchType} from "../../../shared/models/react-big-calendar.ts";

const pageSizes = [25, 50, 100];

const AdminDashboardUsers = () => {
  const [editUserId, setEditUserId] = useState<number | null>(null);
  const [assignUserId, setAssignUserId] = useState<number | null>(null);
  const [eventsUser, setEventsUser] = useState<{ name: string, id: number } | null>(null);
  const [isHireModalOpen, setIsHireModalOpen] = useState(false);
  const [paginationModel, setPaginationModel] = useState({page: 0, pageSize: pageSizes[0]});
  const [selectedRole, setSelectedRole] = useState<UserRole>(UserRole.EMPLOYEE);

  const {openModal, closeModal} = useConfirmationModal();
  const deleteUserMutation = useDeleteUserMutation();

  const {data, isLoading, error} =
    useGetUsers(paginationModel.page, paginationModel.pageSize, selectedRole);

  const handleDelete = (userId: number, userEmail: string) => {
    const onConfirm = () => {
      deleteUserMutation.mutate(userId, {
        onSettled: () => closeModal()
      });
    };
    openModal("Delete User", `Are you sure you want to delete the user: ${userEmail}?`, onConfirm);
  }

  const columns: GridColDef[] = [
    {field: 'firstName', headerName: 'Forename', width: 150},
    {field: 'lastName', headerName: 'Surname', width: 150},
    {field: 'email', headerName: 'Email', width: 200},
    {field: 'salary', headerName: 'Salary', width: 100},
    {field: 'profit', headerName: 'Profit', width: 100},
    {field: 'completedAppointments', headerName: 'Appointments', width: 150},
    {field: 'startDate', headerName: 'Years', width: 100},
    {field: 'beginWorkingHour', headerName: 'Start', width: 100},
    {field: 'endWorkingHour', headerName: 'End', width: 100},
    {field: 'availability', headerName: 'status', width: 100},
    {
      field: 'actions',
      headerName: 'Operations',
      width: 200,
      renderCell: (params) => (
        <div>
          {params.row.salary !== undefined &&
            <IconButton
              onClick={() => setEditUserId(params.row.id)}
              aria-label="edit"
            >
              <EditIcon/>
            </IconButton>
          }
          <IconButton
            onClick={() => handleDelete(params.row.id, params.row.email)}
            aria-label="delete"
          >
            <DeleteIcon/>
          </IconButton>
          {params.row.salary !== undefined &&
            <IconButton
              onClick={() => setAssignUserId(params.row.id)} aria-label="assign"
            >
              <AssignmentIcon/>
            </IconButton>
          }
          <IconButton
            onClick={() => setEventsUser({id: params.row.id, name: params.row.name})}
            aria-label="view appointments"
          >
            <EventIcon/>
          </IconButton>
        </div>
      ),
    },
  ];

  const filteredColumns = columns.filter(column => {
    if (column.field === 'firstName') return data?.content.some(user => user.firstName);
    if (column.field === 'lastName') return data?.content.some(user => user.lastName);
    if (column.field === 'email') return data?.content.some(user => user.email);
    if (column.field === 'salary') return data?.content.some(user => user.staffDetails?.salary);
    if (column.field === 'profit') return data?.content.some(user => user.staffDetails?.profit);
    if (column.field === 'completedAppointments') return data?.content.some(user => user.staffDetails?.completedAppointments);
    if (column.field === 'startDate') return data?.content.some(user => user.staffDetails?.startDate);
    if (column.field === 'beginWorkingHour') return data?.content.some(user => user.staffDetails?.beginWorkingHour);
    if (column.field === 'endWorkingHour') return data?.content.some(user => user.staffDetails?.endWorkingHour);
    if (column.field === 'availability') return data?.content.some(user => user.staffDetails?.isAvailable);
    return true;
  });

  const rows = data ? data.content.map(user => ({
    id: user.id,
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    salary: user.staffDetails?.salary,
    profit: user.staffDetails?.profit,
    completedAppointments: user.staffDetails?.completedAppointments,
    startDate: user.staffDetails?.startDate,
    beginWorkingHour: user.staffDetails?.beginWorkingHour,
    endWorkingHour: user.staffDetails?.endWorkingHour,
    availability: user.staffDetails?.isAvailable ? 'Available' : 'Unavailable',
  })) : [];

  if (isLoading) return <LoadingSpinner/>;
  if (error) return <ErrorPage/>;

  return (
    <div>
      <AdminDashboardHeader
        selectedRole={selectedRole}
        setSelectedRole={setSelectedRole}
        setIsHireModalOpen={setIsHireModalOpen}
      />
      <Box sx={{height: '600px', width: '90%', margin: 'auto'}}>
        <DataGrid
          rows={rows}
          columns={filteredColumns}
          paginationModel={paginationModel}
          onPaginationModelChange={setPaginationModel}
          rowCount={data?.totalElements || 0}
          paginationMode="server"
          pageSizeOptions={pageSizes}
        />
      </Box>
      <ConfirmationModalWrapper/>
      {editUserId && (
        <EditUserModal
          open={!!editUserId}
          onClose={() => setEditUserId(null)}
          userId={editUserId}
        />
      )}
      {assignUserId && (
        <AssignServiceModal
          userId={assignUserId}
          open={!!assignUserId}
          onClose={() => setAssignUserId(null)}
        />
      )}
      {eventsUser &&
        <AdminCalendarModal
          type={FetchType.USER}
          id={eventsUser.id}
          name={eventsUser.name}
          open={!!eventsUser}
          handleClose={() => {
            setEventsUser(null);
          }}
        />
      }
      <HireStaffModal
        open={isHireModalOpen}
        onClose={() => setIsHireModalOpen(false)}
      />
    </div>
  );
};

export default AdminDashboardUsers;