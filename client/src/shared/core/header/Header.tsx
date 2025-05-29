import {AppBar, IconButton, Toolbar, Typography} from "@mui/material";
import {useNavigate} from "react-router-dom";
import {useContext} from "react";
import AssignmentIcon from "@mui/icons-material/Assignment";
import {UserAuthContext} from "../../context/UserAuthContext.tsx";
import UserMenu from "./UserMenu.tsx";
import GuestMenu from "./GuestMenu.tsx";

const Header = () => {
  const navigate = useNavigate();
  const {isUserAuthenticated} = useContext(UserAuthContext);

  return (
    <AppBar position={"sticky"}>
      <Toolbar>
        <IconButton
          size={"large"}
          edge={"start"}
          color={"inherit"}
          aria-label={"logo"}
          onClick={() => navigate("/")}
        >
          <AssignmentIcon/>
        </IconButton>
        <Typography variant={"subtitle1"} flexGrow={1}>
          Flow Appointment Scheduling
        </Typography>
        {isUserAuthenticated() ? <UserMenu/> : <GuestMenu/>}
      </Toolbar>
    </AppBar>
  );
};

export default Header;