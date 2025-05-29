import React, {useContext, useState} from "react";
import {IconButton, Menu, MenuItem} from "@mui/material";
import {useLocation, useNavigate} from "react-router-dom";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import {paths} from "../../paths/paths.ts";
import {UserAuthContext} from "../../context/UserAuthContext.tsx";
import ConfirmationModalWrapper from "../confirm-model/ConfirmationModalWrapper.tsx";
import {useConfirmationModal} from "../../context/ConfirmationModalContext.tsx";
import {UserRole} from "../../models/user.types.ts";

const UserMenu = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { logout, role } = useContext(UserAuthContext);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const { openModal, closeModal } = useConfirmationModal();

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    const onConfirm = () => {
      logout();
      handleClose();
      closeModal();
      navigate("/");
    };
    openModal("Logout", "Are you sure you want to logout?", onConfirm);
  };

  const getPathsByRole = () => {
    switch (role) {
      case UserRole.ADMINISTRATOR:
        return paths.adminPaths;
      case UserRole.CLIENT:
        return paths.clientPaths;
      case UserRole.EMPLOYEE:
        return paths.staffPaths;
      default:
        return [];
    }
  };

  const userPaths = getPathsByRole();

  return (
    <>
      <IconButton
        size={"large"}
        edge={"end"}
        aria-label={"account of current user"}
        aria-controls={"menu-appbar"}
        aria-haspopup={"true"}
        onClick={handleMenu}
        color={"inherit"}
        sx={{
          color: "white",
          backgroundColor: "primary.main",
          "&:hover": { backgroundColor: "primary.dark" }
      }}
      >
        <AccountCircleIcon />
      </IconButton>
      <Menu
        id="menu-appbar"
        anchorEl={anchorEl}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "right",
        }}
        keepMounted
        transformOrigin={{
          vertical: "top",
          horizontal: "right",
        }}
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        {userPaths.map((path, index) => (
          <MenuItem
            key={index}
            onClick={() => {
              navigate(Object.values(path)[0]);
              handleClose();
            }}
            sx={{
              "&:hover": {
                color: "primary.main"
              },
              color: location.pathname === Object.values(path)[0] ? "secondary.main" : "inherit"
            }}
          >
            {Object.keys(path)[0]}
          </MenuItem>
        ))}
        <MenuItem
          onClick={handleLogout}
          sx={{
            "&:hover": {
              color: "primary.main"
            }
          }}
        >
          Logout
        </MenuItem>
      </Menu>
      <ConfirmationModalWrapper />
    </>
  );
};

export default UserMenu;