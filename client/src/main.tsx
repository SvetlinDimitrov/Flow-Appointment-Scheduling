import { createTheme, ThemeProvider } from '@mui/material';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.tsx';
import './reset.css';
import './index.css';
import UserAuthProvider from "./shared/context/UserAuthContext.tsx";
import { QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { BrowserRouter } from "react-router-dom";
import { queryClient } from "./utils/react-query/queryClient.ts";
import ToastNotification from "./shared/core/toast/ToastNotification.tsx";
import { ConfirmationModalProvider } from "./shared/context/ConfirmationModalContext.tsx";
import "react-big-calendar/lib/css/react-big-calendar.css";


declare module '@mui/material/styles/createPalette' {
  interface PaletteOptions {
    flowBgColor?: PaletteOptions['primary'];
  }
}

/**
 * MUI Theme Customizations:
 * - Section: {section header - h2, section description - h5}
 * - Form: {form header - h4}
 * - Header and Footer: {subtitle1}
 * - Card: {header - h5, description - subtitle2, more - subtitle1}
 */
const theme = createTheme({
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 900,
      lg: 1200,
      xl: 1536,
    },
  },
  palette: {
    flowBgColor: {
      main: '#eeeeee',
    },
  },
  typography: {
    fontFamily: 'Arial, sans-serif',
    h2: {
      '@media (max-width:600px)': {
        fontSize: '2.6rem',
      },
    },
    h5: {
      '@media (max-width:600px)': {
        fontSize: '1.2rem',
      },
    },
    subtitle1: {
      '@media (max-width:600px)': {
        fontSize: '0.8rem',
      },
    }
  },
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <UserAuthProvider>
        <ConfirmationModalProvider>
          <QueryClientProvider client={queryClient}>
            <BrowserRouter>
              <App />
            </BrowserRouter>
            <ReactQueryDevtools />
            <ToastNotification />
          </QueryClientProvider>
        </ConfirmationModalProvider>
      </UserAuthProvider>
    </ThemeProvider>
  </StrictMode>,
);