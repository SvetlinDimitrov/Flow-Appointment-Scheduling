import {JwtToken, RefreshToken} from "../auth.types.ts";

export interface AuthenticationRequest {
  email: string;
  password: string;
}

export interface AuthenticationResponse {
  jwtToken: JwtToken;
  refreshToken: RefreshToken;
}

export interface RefreshTokenRequest {
  token: string;
}

export interface UserPasswordUpdate {
  newPassword: string;
}