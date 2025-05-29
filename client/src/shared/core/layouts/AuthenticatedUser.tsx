import {Navigate, Outlet} from "react-router-dom";
import {useContext} from "react";
import {UserAuthContext} from "../../context/UserAuthContext.tsx";

const AuthenticatedUser = () => {
  const {isUserAuthenticated} = useContext(UserAuthContext);

  if (!isUserAuthenticated()) {
    return <Navigate to="/login" />;
  }

  return <Outlet />;
};

export default AuthenticatedUser;