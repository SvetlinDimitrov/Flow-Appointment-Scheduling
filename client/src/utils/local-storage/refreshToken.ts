import {RefreshToken} from "../../shared/models/auth.types.ts";

export const setRefreshTokenInLocalStorage = (token: RefreshToken) => {
  localStorage.setItem('flow/refreshToken', JSON.stringify(token));
};

export const getRefreshTokenFromLocalStorage = (): RefreshToken | null => {
  const storedToken = localStorage.getItem('flow/refreshToken');
  return storedToken ? JSON.parse(storedToken) : null;
}

export const removeRefreshTokenFromLocalStorage = () => {
  localStorage.removeItem('flow/refreshToken');
};