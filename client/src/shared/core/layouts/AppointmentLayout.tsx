import ClientAppointmentInfo from "../../../features/appointment/appointment-client/ClientAppointmentInfo.tsx";
import {useContext} from "react";
import {UserAuthContext} from "../../context/UserAuthContext.tsx";
import StaffAppointmentInfo from "../../../features/appointment/appointment-staff/StaffAppointmentInfo.tsx";
import {UserRole} from "../../models/user.types.ts";

const AppointmentLayout = () => {
  const {role} = useContext(UserAuthContext);

  if (role === UserRole.EMPLOYEE) {
    return <StaffAppointmentInfo/>;
  } else {
    return <ClientAppointmentInfo/>;
  }
};

export default AppointmentLayout;