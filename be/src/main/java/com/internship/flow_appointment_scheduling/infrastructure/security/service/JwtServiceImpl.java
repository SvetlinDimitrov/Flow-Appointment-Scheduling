package com.internship.flow_appointment_scheduling.infrastructure.security.service;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.events.passowrd_reset.PasswordResetEvent;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.RefreshTokenMapper;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtView;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenPostRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenView;
import com.internship.flow_appointment_scheduling.infrastructure.security.entity.RefreshToken;
import com.internship.flow_appointment_scheduling.infrastructure.security.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  private final ApplicationEventPublisher eventPublisher;

  private final RefreshTokenMapper refreshTokenMapper;

  @Value("${refresh-token.expiration-time}")
  private long refreshTokenDuration;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration-time}")
  private long expirationTime;

  public AuthenticationResponse refreshToken(RefreshTokenPostRequest dto) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findById(dto.token())
            .orElseThrow(
                () -> new NotFoundException(Exceptions.REFRESH_TOKEN_NOT_FOUND, dto.token()));

    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new BadRequestException(Exceptions.REFRESH_TOKEN_EXPIRED);
    }

    JwtView jwtView = generateJwtToken(refreshToken.getUser());
    RefreshTokenView refreshTokenView = refreshTokenMapper.toView(refreshToken);

    return new AuthenticationResponse(jwtView, refreshTokenView);
  }

  public AuthenticationResponse generateToken(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException(Exceptions.USER_NOT_FOUND_BY_EMAIL, email));

    JwtView jwtView = generateJwtToken(user);
    RefreshTokenView refreshTokenView = generateRefreshToken(user);

    return new AuthenticationResponse(jwtView, refreshTokenView);
  }

  public Boolean isJwtTokenExpired(String token) {
    try {
      Date expiration = extractAllClaims(token).getExpiration();
      return expiration.before(new Date());
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      return true;
    }
  }

  public String getEmailFromToken(String token) {
    Claims claims = extractAllClaims(token);
    return claims.getSubject();
  }

  /**
   * Sends an email for resetting the password. First, this method checks if there is an existing
   * user with the provided email. If the user exists, it checks if the user already has an existing
   * reset token. If a valid reset token is found, an exception is thrown to prevent spamming the
   * request. If there is no valid reset token, a new one is created with a 15-minute lifespan. The
   * reset token is then sent to the user's email. When the user clicks the link in the email, they
   * will be redirected to a specific page in the frontend.
   *
   * @param email the email of the user requesting the password reset
   * @throws NotFoundException if the user is not found by the provided email
   * @throws BadRequestException if a valid reset token already exists
   */
  @Override
  public void sendEmailForRestingThePassword(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException(Exceptions.USER_NOT_FOUND_BY_EMAIL, email));

    if (user.getPasswordResetToken() != null) {
      if (!isJwtTokenExpired(user.getPasswordResetToken())) {
        throw new BadRequestException(Exceptions.RESET_TOKEN_ALREADY_EXISTS);
      }
    }

    String token = generateShortLivedToken(email);

    user.setPasswordResetToken(token);

    userRepository.save(user);

    eventPublisher.publishEvent(new PasswordResetEvent(this, token, email));
  }

  private String generateShortLivedToken(String userEmail) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + 15 * 60 * 1000);

    return Jwts.builder()
        .setSubject(userEmail)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private JwtView generateJwtToken(User user) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationTime);

    String token =
        Jwts.builder()
            .setSubject(user.getEmail())
            .claim("userId", user.getId())
            .claim("role", user.getRole())
            .setIssuedAt(now)
            .setExpiration(expirationDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();

    LocalDateTime expirationTime =
        LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());

    return new JwtView(token, expirationTime);
  }

  private RefreshTokenView generateRefreshToken(User user) {

    if (user.getRefreshToken() != null) {
      refreshTokenRepository.delete(user.getRefreshToken());
    }

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
