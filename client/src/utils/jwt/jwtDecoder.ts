import {jwtDecode} from 'jwt-decode';
import {UserRole} from "../../shared/models/user.types.ts";

interface JwtPayload {
  userId: string;
  role: UserRole;
}

interface JwtPayloadWithExp {
  exp: number;
}

export const getUserIdFromJwt = (jwtToken: string): number => {
  const decoded = jwtDecode<JwtPayload>(jwtToken);
  return parseInt(decoded.userId);
};

export const getUserRoleFromJwt = (jwtToken: string): UserRole => {
  const decoded = jwtDecode<JwtPayload>(jwtToken);
  return decoded.role;
};

export const isJwtValid = (jwtToken: string | null): boolean => {
  if (!jwtToken) return false;
  try {
    const decoded = jwtDecode<JwtPayloadWithExp>(jwtToken);
    const expirationDate = new Date(decoded.exp * 1000);
    console.log(expirationDate);
    return expirationDate > new Date();
  } catch {
    return false;
  }
};

