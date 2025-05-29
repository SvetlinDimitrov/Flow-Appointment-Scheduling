import {createContext, ReactNode, useCallback, useState} from 'react';
import {
  getRefreshTokenFromLocalStorage,
  removeRefreshTokenFromLocalStorage,
  setRefreshTokenInLocalStorage
} from "../../utils/local-storage/refreshToken.ts";

import {
  getJwtTokenFromLocalStorage,
  removeJwtTokenFromLocalStorage,
  setJwtTokenInLocalStorage
} from "../../utils/local-storage/jwtToken.ts";
import {getUserIdFromJwt, getUserRoleFromJwt} from "../../utils/jwt/jwtDecoder.ts";
import {JwtToken, RefreshToken} from "../models/auth.types.ts";
import {UserRole} from "../models/user.types.ts";

interface UserContextType {
  userId: number | null;
  role: UserRole | null;
  login: (jwtToken: JwtToken, refreshToken: RefreshToken) => void;
  logout: () => void;
  isUserAuthenticated: () => boolean;
}

const initialValues: UserContextType = {
  userId: null,
  role: null,
  login: () => {},
  logout: () => {},
  isUserAuthenticated: () => false,
}


export const UserAuthContext = createContext<UserContextType>(initialValues);

export const UserAuthProvider = ({children}: { children: ReactNode }) => {

  const [userId, setUserId] = useState<number | null>(() => {
    const jwt: JwtToken = getJwtTokenFromLocalStorage()!;
    if (jwt) return getUserIdFromJwt(jwt.token);
    return null;
  });
  const [role, setRole] = useState<UserRole | null>(() => {
    const jwt: JwtToken = getJwtTokenFromLocalStorage()!;
    if (jwt) return getUserRoleFromJwt(jwt.token);
    return null;
  });

  const isUserAuthenticated = () => {
    const refreshToken: (RefreshToken | null) = getRefreshTokenFromLocalStorage();
    return !!(refreshToken && new Date(refreshToken.expirationTime) > new Date());
  };

  const login = useCallback((jwtToken: JwtToken, refreshToken: RefreshToken) => {
    setRefreshTokenInLocalStorage(refreshToken);
    setJwtTokenInLocalStorage(jwtToken);
    const userId = getUserIdFromJwt(jwtToken.token);
    setUserId(userId);
    const role = getUserRoleFromJwt(jwtToken.token);
    setRole(role);
  }, []);

  const logout = useCallback(() => {
    removeRefreshTokenFromLocalStorage();
    removeJwtTokenFromLocalStorage();
    setUserId(null);
    setRole(null);
  }, []);

  return (
    <UserAuthContext.Provider value={{
      userId,
      role,
      login,
      logout,
      isUserAuthenticated,
    }}>
      {children}
    </UserAuthContext.Provider>
  );
};

export default UserAuthProvider;
