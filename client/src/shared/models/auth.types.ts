export interface JwtToken {
  token: string;
  expirationTime: Date;
}

export interface RefreshToken {
  token: string;
  expirationTime: Date;
}