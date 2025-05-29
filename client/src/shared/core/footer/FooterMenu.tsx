import React, { useState } from 'react';
import { IconButton, Menu, MenuItem, Typography } from '@mui/material';
import { Link } from 'react-router-dom';
import MenuIcon from '@mui/icons-material/Menu';
import { paths } from "../../paths/paths.ts";

const FooterMenu = () => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <>
      <IconButton
        size="large"
        edge="end"
        color="inherit"
        aria-label="menu"
        onClick={handleMenu}
      >
        <MenuIcon />
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        keepMounted
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        {paths.defaultPaths.map((path, index) => (
          <MenuItem
            key={index}
            onClick={() => {
              handleClose();
            }}
            component={Link}
            to={Object.values(path)[0]}
            sx={{
              "&:hover": {
                color: "primary.main"
              },
              color: location.pathname === Object.values(path)[0] ? "secondary.main" : "inherit"
            }}
          >
            <Typography variant="subtitle1">
              {Object.keys(path)[0]}
            </Typography>
          </MenuItem>
        ))}
      </Menu>
    </>
  );
};

export default FooterMenu;