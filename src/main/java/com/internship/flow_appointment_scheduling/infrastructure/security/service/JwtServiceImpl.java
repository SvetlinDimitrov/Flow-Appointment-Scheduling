package com.internship.flow_appointment_scheduling.infrastructure.security.service;

import com.internship.flow_appointment_scheduling.features.user.entity.RefreshToken;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.RefreshTokenExpiredException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.RefreshTokenNotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.UserNotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.RefreshTokenMapper;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtView;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenPostRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenView;
import com.internship.flow_appointment_scheduling.infrastructure.security.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

  @Value("${refresh-token.expiration-time}")
  private long refreshTokenDuration;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration-time}")
  private long expirationTime;

  private final RefreshTokenRepository refreshTokenRepository;
  private final RefreshTokenMapper refreshTokenMapper;
  private final UserRepository userRepository;

  public JwtServiceImpl(RefreshTokenRepository refreshTokenRepository, RefreshTokenMapper refreshTokenMapper, UserRepository userRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.refreshTokenMapper = refreshTokenMapper;
    this.userRepository = userRepository;
  }

  public JwtResponse refreshToken(RefreshTokenPostRequest dto) {
    RefreshToken refreshToken = refreshTokenRepository
        .findById(dto.token())
        .orElseThrow(() -> new RefreshTokenNotFoundException(dto.token()));

    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new RefreshTokenExpiredException();
    }

    JwtView jwtView = generateJwtToken(refreshToken.getUser().getEmail());
    RefreshTokenView refreshTokenView = refreshTokenMapper.toView(refreshToken);

    return new JwtResponse(jwtView, refreshTokenView);
  }

  public JwtResponse generateToken(String email) {
    JwtView jwtView = generateJwtToken(email);
    RefreshTokenView refreshTokenView = generateRefreshToken(email);

    return new JwtResponse(jwtView, refreshTokenView);
  }

  public Boolean isJwtTokenExpired(String token) {
    Date expiration = extractAllClaims(token).getExpiration();
    return expiration.before(new Date());
  }

  public String getEmailFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.getSubject();
  }

  private JwtView generateJwtToken(String email) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationTime);

    String token = Jwts.builder()
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();

    LocalDateTime expirationTime = LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());

    return new JwtView(token, expirationTime);
  }

  private RefreshTokenView generateRefreshToken(String email) {
    User user = userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));

    if (user.getRefreshToken() != null) refreshTokenRepository.delete(user.getRefreshToken());

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiryDate = now.plusSeconds(refreshTokenDuration / 1000);
    refreshToken.setExpiryDate(expiryDate);

    refreshTokenRepository.save(refreshToken);
    return refreshTokenMapper.toView(refreshToken);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    byte[] keyBytes = secret.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
