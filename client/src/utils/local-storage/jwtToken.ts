import {JwtToken} from "../../shared/models/auth.types.ts";

export const setJwtTokenInLocalStorage = (token: JwtToken) => {
  localStorage.setItem('flow/jwtToken', JSON.stringify(token));
}

export const getJwtTokenFromLocalStorage = (): JwtToken | null => {
  const storedJwtToken = localStorage.getItem('flow/jwtToken');
  return storedJwtToken ? JSON.parse(storedJwtToken) : null;
}

export const removeJwtTokenFromLocalStorage = () => {
  localStorage.removeItem('flow/jwtToken');
}