import {Route, Routes} from "react-router-dom";
import Home from "./shared/core/home/Home.tsx";
import GuestRoutes from "./shared/core/layouts/GuestRoutes.tsx";
import Login from "./features/guest/login/Login.tsx";
import Register from "./features/guest/register/Register.tsx";
import PageNotFound from "./shared/core/not-found/PageNotFound.tsx";
import AuthenticatedUser from "./shared/core/layouts/AuthenticatedUser.tsx";
import Header from "./shared/core/header/Header.tsx";
import Footer from "./shared/core/footer/Footer.tsx";
import {setupInterceptors} from "./utils/axios-config/axiosInstance.ts";
import {useContext, useEffect} from "react";
import Profile from "./features/users/profile/Profile.tsx";
import AboutUs from "./features/guest/about-us/AboutUs.tsx";
import {UserAuthContext} from "./shared/context/UserAuthContext.tsx";
import ContactUs from "./features/guest/contact-us/ContactUs.tsx";
import {Box} from "@mui/material";
import AdminRoutes from "./shared/core/layouts/AdminRoutes.tsx";
import AdminServiceDashboard from "./features/admin/service-dashboard/AdminServiceDashboard.tsx";
import AdminDashboardUsers from "./features/admin/user-dashboard/AdminDashboardUsers.tsx";
import ServicePage from "./features/guest/service-id/ServiceIdPage.tsx";
import StaffClientRoutes from "./shared/core/layouts/StaffClientRoutes.tsx";
import AppointmentLayout from "./shared/core/layouts/AppointmentLayout.tsx";
import ErrorPage from "./shared/core/error-page/ErrorPage.tsx";
import PasswordResetForm from "./features/guest/reset-password/PasswordResetForm.tsx";

function App() {

  const {login, logout} = useContext(UserAuthContext);

  useEffect(() => {
    setupInterceptors({login, logout});
  }, [login, logout]);

  return (
    <Box display="flex" flexDirection="column" minHeight="100vh" minWidth={"280px"}>
      <Header/>
      <Box flexGrow={1}>
        <Routes>
          <Route path="/" element={<Home/>}/>
          <Route path={"/error"} element={<ErrorPage/>}/>
          <Route path="/about-us" element={<AboutUs/>}/>
          <Route path="/contact-us" element={<ContactUs/>}/>
          <Route element={<GuestRoutes/>}>
            <Route path="/login" element={<Login/>}/>
            <Route path="/secret-login" element={<Login/>}/>
            <Route path="/reset-password" element={<PasswordResetForm/>}/>
            <Route path="/register" element={<Register/>}/>
          </Route>
          <Route element={<AuthenticatedUser/>}>
            <Route element={<AdminRoutes/>}>
              <Route path="/admin/services" element={<AdminServiceDashboard/>}/>
              <Route path="/admin/users" element={<AdminDashboardUsers/>}/>
            </Route>
            <Route element={<StaffClientRoutes/>}>
              <Route path="/appointments" element={<AppointmentLayout/>}/>
            </Route>
            <Route path="/profile" element={<Profile/>}/>
          </Route>
          <Route path="/service/:id" element={<ServicePage/>} />
          <Route path="*" element={<PageNotFound/>}/>
        </Routes>
      </Box>
      <Footer/>
    </Box>
  )
}

export default App;