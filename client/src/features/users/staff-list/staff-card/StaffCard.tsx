import {Box, Card, CardContent, Typography} from "@mui/material";
import UserCardActions from "./user/UserCardActions.tsx";
import AdminCardActions from "./admin/AdminCardActions.tsx";
import {User} from "../../../../shared/models/user.types.ts";
import {DateTime} from "luxon";

export interface StaffCardProps {
  employee: User;
  handleDeleteEmployeeFromService: (() => void) | undefined;
  handleBookWithStaff: (() => void) | undefined;
}

const StaffCard = (props: StaffCardProps) => {
  const {handleDeleteEmployeeFromService, handleBookWithStaff} = props;
  const {employee} = props;

  return (
    <Box>
      <Card>
        <CardContent sx={{maxWidth: '200px', minWidth: '200px'}}>
          <Box
            component="div"
            width={'100%'}
            height={'150px'}
            mb={2}
            sx={{
              objectFit: 'cover',
              overflow: 'hidden',
              backgroundColor: '#f0f0f0',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#aaa',
              fontSize: '16px'
            }}
          >
            No Image Available
          </Box>
          <Typography
            variant={"h6"}
            fontWeight={'bold'}
            sx={{
              wordWrap: 'break-word',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap'
            }}
          >
            {employee.firstName} {employee.lastName}
          </Typography>
          <Typography
            variant={"subtitle2"}
            color={'gray'}
            mt={1}
            sx={{
              wordWrap: 'break-word',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap'
            }}
          >
            {employee.email}
          </Typography>
          {employee.staffDetails && <>
            <Typography
              variant={"subtitle2"}
              color={'gray'}
              mt={1}
              sx={{
                wordWrap: 'break-word',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap'
              }}
            >
              Hours: {DateTime.fromISO(employee.staffDetails.beginWorkingHour.toString()).toFormat('HH:mm')} - {DateTime.fromISO(employee.staffDetails.endWorkingHour.toString()).toFormat('HH:mm')}
            </Typography>
            <Typography
              variant={"subtitle2"}
              color={employee.staffDetails.isAvailable ? 'green' : 'red'}
              mt={1}
              sx={{
                wordWrap: 'break-word',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap'
              }}
            >
              {employee.staffDetails.isAvailable ? 'Available' : 'Not Available'}
            </Typography>
          </>
          }
        </CardContent>
        {handleDeleteEmployeeFromService ? (
          <AdminCardActions
            onDelete={handleDeleteEmployeeFromService}
          />
        ) : handleBookWithStaff ? (
          <UserCardActions
            bookWithStaff={handleBookWithStaff}
            employeeFirstName={employee.firstName}
            available={employee.staffDetails?.isAvailable || false}
          />
        ) : null}
      </Card>
    </Box>);
};

export default StaffCard;