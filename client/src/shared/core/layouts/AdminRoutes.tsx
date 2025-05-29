import {useContext} from "react";
import {UserAuthContext} from "../../context/UserAuthContext.tsx";
import {Navigate, Outlet} from "react-router-dom";
import {UserRole} from "../../models/user.types.ts";

const AdminRoutes = () => {
  const {role} = useContext(UserAuthContext);

  if (role !== UserRole.ADMINISTRATOR) {
    return <Navigate to="/"/>;
  }

  return <Outlet/>;
};

export default AdminRoutes;