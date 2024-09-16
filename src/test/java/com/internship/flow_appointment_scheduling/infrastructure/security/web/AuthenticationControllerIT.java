package com.internship.flow_appointment_scheduling.infrastructure.security.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.flow_appointment_scheduling.FlowAppointmentSchedulingApplication;
import com.internship.flow_appointment_scheduling.config.TestContainersConfig;
import com.internship.flow_appointment_scheduling.enums.Users;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.mail_service.MailService;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtView;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenPostRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenView;
import com.internship.flow_appointment_scheduling.infrastructure.security.entity.RefreshToken;
import com.internship.flow_appointment_scheduling.infrastructure.security.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
@ContextConfiguration(classes = {TestContainersConfig.class,
    FlowAppointmentSchedulingApplication.class})
@AutoConfigureMockMvc
class AuthenticationControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RefreshTokenRepository refreshTokenRepository;
  @MockBean
  private MailService mailService;

  private static AuthenticationRequest VALID_AUTHENTICATION_REQUEST;
  private static User VALID_STAFF;
  @Value("${jwt.secret}")
  private String secret;

  @BeforeEach
  void setUp() {
    VALID_AUTHENTICATION_REQUEST = new AuthenticationRequest(
        Users.STAFF.getEmail(),
        "password123A!"
    );

    VALID_STAFF = userRepository.findByEmail(Users.STAFF.getEmail())
        .orElseThrow(() -> new RuntimeException("Staff not found"));
  }

  @Test
  void createAuthenticationToken_returnsOk_whenValidRequest() throws Exception {
    String response = mockMvc.perform(post("/api/v1/auth")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(VALID_AUTHENTICATION_REQUEST)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AuthenticationResponse authResponse = objectMapper.readValue(response,
        AuthenticationResponse.class);
    JwtView jwtToken = authResponse.jwtToken();
    RefreshTokenView refreshToken = authResponse.refreshToken();

    String[] jwtParts = jwtToken.token().split("\\.");
    String jwtPayload = new String(java.util.Base64.getDecoder().decode(jwtParts[1]));
    assertTrue(jwtPayload.contains("\"sub\":\"" + VALID_STAFF.getEmail() + "\""));
    assertTrue(jwtPayload.contains("\"userId\":" + VALID_STAFF.getId()));
    assertTrue(jwtPayload.contains("\"role\":\"" + VALID_STAFF.getRole() + "\""));

    assertFalse(refreshToken.expirationTime().isBefore(LocalDateTime.now()));
    assertFalse(jwtToken.expirationTime().isBefore(LocalDateTime.now()));
  }

  @Test
  void createAuthenticationToken_returnsForbidden_whenInvalidRequest() throws Exception {
    AuthenticationRequest invalidRequest = new AuthenticationRequest("invalid@example.com",
        "invalidPassword");

    mockMvc.perform(post("/api/v1/auth")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void refreshToken_returnsOk_whenValidToken() throws Exception {
    RefreshToken validRefreshToken = new RefreshToken();
    validRefreshToken.setUser(VALID_STAFF);
    validRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

    refreshTokenRepository.save(validRefreshToken);
    RefreshTokenPostRequest request = new RefreshTokenPostRequest(validRefreshToken.getId());

    String response = mockMvc.perform(post("/api/v1/auth/refresh")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AuthenticationResponse authResponse = objectMapper.readValue(response,
        AuthenticationResponse.class);
    JwtView jwtToken = authResponse.jwtToken();
    RefreshTokenView refreshToken = authResponse.refreshToken();

    String[] jwtParts = jwtToken.token().split("\\.");
    String jwtPayload = new String(java.util.Base64.getDecoder().decode(jwtParts[1]));
    assertTrue(jwtPayload.contains("\"sub\":\"" + VALID_STAFF.getEmail() + "\""));
    assertTrue(jwtPayload.contains("\"userId\":" + VALID_STAFF.getId()));
    assertTrue(jwtPayload.contains("\"role\":\"" + VALID_STAFF.getRole() + "\""));

    assertFalse(refreshToken.expirationTime().isBefore(LocalDateTime.now()));
  }

  @Test
  void refreshToken_returnsBadRequest_whenRefreshTokenExpired() throws Exception {
    RefreshToken expiredRefreshToken = new RefreshToken();
    refreshTokenRepository.save(expiredRefreshToken);
    expiredRefreshToken.setUser(VALID_STAFF);
    expiredRefreshToken.setExpiryDate(LocalDateTime.now().minusDays(1));

    RefreshTokenPostRequest request = new RefreshTokenPostRequest(expiredRefreshToken.getId());

    mockMvc.perform(post("/api/v1/auth/refresh")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void refreshToken_returnsNotFound_whenTokenDoesNotExist() throws Exception {
    String invalidTokenId = "invalidTokenId";
    RefreshTokenPostRequest request = new RefreshTokenPostRequest(invalidTokenId);

    mockMvc.perform(post("/api/v1/auth/refresh")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void resetPassword_returnsOk_whenValidEmail() throws Exception {
    mockMvc.perform(get("/api/v1/auth/reset-password")
            .param("email", VALID_STAFF.getEmail()))
        .andExpect(status().isOk());

    verify(mailService, times(1))
        .sendResetPasswordEmail(any(String.class), any(String.class));
  }

  @Test
  void resetPassword_returnsNotFound_whenEmailNotFound() throws Exception {
    String invalidEmail = "invalidEmail";
    mockMvc.perform(get("/api/v1/auth/reset-password")
            .param("email", invalidEmail))
        .andExpect(status().isNotFound());
  }

  @Test
  void resetPassword_returnsBadRequest_whenResetTokenAlreadyExists() throws Exception {
    String validJwtToken = Jwts.builder()
        .setSubject(VALID_STAFF.getEmail())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
        .compact();

    VALID_STAFF.setPasswordResetToken(validJwtToken);
    userRepository.save(VALID_STAFF);

    mockMvc.perform(get("/api/v1/auth/reset-password")
            .param("email", VALID_STAFF.getEmail()))
        .andExpect(status().isBadRequest());
  }
}