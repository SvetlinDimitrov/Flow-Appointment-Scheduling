import {Box} from "@mui/material";
import WelcomeSection from "./welcome_section/WelcomeSection.tsx";
import {useContext, useEffect} from "react";
import useGetUserQuery from "../../../../hooks/users/query/useGetUserQuery.ts";
import {UserAuthContext} from "../../../context/UserAuthContext.tsx";
import LoadingSpinner from "../../loading/main-loader/LoadingSpinner.tsx";
import PageNotFound from "../../not-found/PageNotFound.tsx";
import {UserRole} from "../../../models/user.types.ts";
import {useNavigate} from "react-router-dom";
import ServiceGuestAuthSection
  from "../../../../features/guest/landing-page/service-section/ServiceGuestAuthSection.tsx";

const AuthHome = () => {
  const navigate = useNavigate();

  const {userId, role} = useContext(UserAuthContext);

  useEffect(() => {
    if (role === UserRole.ADMINISTRATOR) {
      navigate('/admin/services');
    } else if (role === UserRole.EMPLOYEE) {
      navigate('/appointments');
    }
  }, [role, navigate]);

  const {data, isLoading, error} = useGetUserQuery(userId);


  if (isLoading || !data) return <LoadingSpinner/>;
  if (error) return <PageNotFound/>;
  if (role === UserRole.ADMINISTRATOR || role === UserRole.EMPLOYEE) return null;

  return (
    <Box>
      <WelcomeSection user={data}/>
      <ServiceGuestAuthSection
        title={"Services"}
        description={"Choose from a variety of services"}
        buttonText={"Book now"}
      />
    </Box>
  );
};

export default AuthHome;