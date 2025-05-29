import {Navigate, Outlet} from "react-router-dom";
import {useContext} from "react";
import {UserAuthContext} from "../../context/UserAuthContext.tsx";

const GuestRoutes = () => {
  const {isUserAuthenticated} = useContext(UserAuthContext);

  if (isUserAuthenticated()) {
    return <Navigate to="/"/>;
  }

  return <Outlet/>;
};

export default GuestRoutes;