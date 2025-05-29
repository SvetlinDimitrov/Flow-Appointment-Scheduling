import {AppBar, Box, Toolbar, Typography, useMediaQuery, useTheme} from '@mui/material';
import {Link} from 'react-router-dom';
import {paths} from "../../paths/paths.ts";
import FooterMenu from './FooterMenu';

const Footer = () => {
  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <AppBar position={'static'} sx={{top: 'auto', bottom: 0}}>
      <Toolbar sx={{display: 'flex', justifyContent: 'center', gap: 2}}>
        <Typography variant={"subtitle1"} color={"inherit"}>
          © 2024 Prime Holding
        </Typography>
        {isSmallScreen ? (
          <FooterMenu/>
        ) : (
          <Box display={'flex'} gap={2}>
            {paths.defaultPaths.map((path, index) => (
              <Link key={index}
                    to={Object.values(path)[0]}
                    style={{textDecoration: 'none', color: 'inherit'}}
              >
                <Typography variant={"subtitle1"} color={"inherit"}>
                  {Object.keys(path)[0]}
                </Typography>
              </Link>
            ))}
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Footer;