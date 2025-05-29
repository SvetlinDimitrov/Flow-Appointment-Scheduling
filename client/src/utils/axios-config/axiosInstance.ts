import axios from 'axios';
import {refreshToken} from "../../services/auth-service.ts";
import {getRefreshTokenFromLocalStorage} from "../local-storage/refreshToken.ts";
import {getJwtTokenFromLocalStorage} from "../local-storage/jwtToken.ts";
import {JwtToken, RefreshToken} from "../../shared/models/auth.types.ts";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

interface InterceptorProps {
  login: (jwtToken: JwtToken, refreshToken: RefreshToken) => void;
  logout: () => void;
}

const setupInterceptors = ({login, logout}: InterceptorProps) => {
  axiosInstance.interceptors.request.use(
    async (config) => {

      const refreshTokenValue = getRefreshTokenFromLocalStorage();
      const jwtToken = getJwtTokenFromLocalStorage();
      if (refreshTokenValue) {
        if (new Date(refreshTokenValue.expirationTime) <= new Date()) {
          // Refresh token is expired
          logout();
        } else {
          // Refresh token is valid
          if (jwtToken) {
            if (new Date(jwtToken.expirationTime) <= new Date()) {
              // JWT token is expired, refresh it
              try {
                const response = await refreshToken({ token: refreshTokenValue.token });
                login(response.jwtToken, refreshTokenValue);
                config.headers['Authorization'] = `Bearer ${response.jwtToken.token}`;
                // eslint-disable-next-line @typescript-eslint/no-unused-vars
              } catch (error) {
                logout();
              }
            } else {
              // JWT token is valid
              config.headers['Authorization'] = `Bearer ${jwtToken.token}`;
            }
          }
        }
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
};

export { axiosInstance, setupInterceptors };
