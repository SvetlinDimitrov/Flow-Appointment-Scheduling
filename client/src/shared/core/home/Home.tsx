import {useContext} from "react";
import {UserAuthContext} from "../../context/UserAuthContext.tsx";
import GuestHome from "../../../features/guest/landing-page/GuestHome.tsx";
import AuthHome from "./auth-home/AuthHome.tsx";

const Home = () => {
  const { isUserAuthenticated } = useContext(UserAuthContext);

  if (isUserAuthenticated()) return <AuthHome/>;
  return <GuestHome />;
};

export default Home;