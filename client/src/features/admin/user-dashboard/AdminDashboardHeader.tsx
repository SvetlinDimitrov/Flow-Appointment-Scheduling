import {Box, Button, FormControl, InputLabel, MenuItem, Select, SelectChangeEvent, Typography} from '@mui/material';
import {UserRole} from '../../../shared/models/user.types.ts';

interface AdminDashboardHeaderProps {
  selectedRole: UserRole;
  setSelectedRole: (role: UserRole) => void;
  setIsHireModalOpen: (open: boolean) => void;
}

const AdminDashboardHeader =
  ({
    selectedRole,
    setSelectedRole,
    setIsHireModalOpen
   }: AdminDashboardHeaderProps) => {
  const handleRoleChange = (event: SelectChangeEvent<UserRole>) => {
    setSelectedRole(event.target.value as UserRole);
  };

  return (
    <Box
      p={1}
      display="flex"
      flexDirection="row"
      alignItems="center"
      justifyContent="space-between"
      width={'90%'}
      margin={'auto'}
      mt={6}
      gap={2}
      sx={{
        flexDirection: {xs: 'column', md: 'row'},
      }}
    >
      <Typography
        variant={'h4'}
        align={'left'}
        sx={{
          fontSize: {xs: '1.8rem', sm: '2.6rem'},
        }}
      >
        Users Dashboard
      </Typography>
      <Box
        display="flex"
        alignItems="center"
        gap={2}
        sx={{
          flexDirection: {xs: 'column', sm: 'row'},
        }}
      >
        <Button
          variant="outlined"
          color="primary"
          onClick={() => setIsHireModalOpen(true)}
        >
          Hire Staff Member
        </Button>
        <FormControl sx={{minWidth: 200}}>
          <InputLabel id="user-role-select-label">User Role</InputLabel>
          <Select
            variant={'outlined'}
            size={'small'}
            labelId="user-role-select-label"
            value={selectedRole}
            onChange={handleRoleChange}
            label="User Role"
          >
            <MenuItem value={UserRole.ADMINISTRATOR}>Administrator</MenuItem>
            <MenuItem value={UserRole.EMPLOYEE}>Employee</MenuItem>
            <MenuItem value={UserRole.CLIENT}>Client</MenuItem>
          </Select>
        </FormControl>
      </Box>
    </Box>
  );
};

export default AdminDashboardHeader;