import React, { useState } from "react";
import { IconButton, Menu, MenuItem, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import {paths} from "../../paths/paths.ts";

const GuestMenu = () => {
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(anchorEl ? null : event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

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
          color: "white", backgroundColor: "primary.main",
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
        {paths.guestPaths.map((path, index) => (
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
            <Typography
              variant={"subtitle1"}
              sx={{
                color: "inherit",
                cursor: "pointer"
              }}
            >
              {Object.keys(path)[0]}
            </Typography>
          </MenuItem>
        ))}
      </Menu>
    </>
  );
};

export default GuestMenu;