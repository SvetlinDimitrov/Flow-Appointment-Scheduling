package com.internship.flow_appointment_scheduling.infrastructure.security.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.internship.flow_appointment_scheduling.FlowAppointmentSchedulingApplication;
import com.internship.flow_appointment_scheduling.config.TestContainersConfig;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.AuthenticationResponse;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtView;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenPostRequest;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.RefreshTokenView;
import com.internship.flow_appointment_scheduling.infrastructure.security.entity.RefreshToken;
import com.internship.flow_appointment_scheduling.infrastructure.security.repository.RefreshTokenRepository;
import com.internship.flow_appointment_scheduling.seed.enums.SeededStaffUsers;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.util.Date;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "duke"))
      .withPerMethodLifecycle(true);

  private static AuthenticationRequest validAuthenticationRequest;
  private static User validStaff;
  @Value("${jwt.secret}")
  private String secret;

  @BeforeEach
  void setUp() {
    validAuthenticationRequest = new AuthenticationRequest(
        SeededStaffUsers.STAFF1.getEmail(),
        "password123A!"
    );

    validStaff = userRepository.findByEmail(SeededStaffUsers.STAFF1.getEmail())
        .orElseThrow(() -> new RuntimeException("Staff not found"));
  }

  @Test
  void createAuthenticationToken_returnsOk_whenValidRequest() throws Exception {
    String response = mockMvc.perform(post("/api/v1/auth")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(validAuthenticationRequest)))
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
    assertTrue(jwtPayload.contains("\"sub\":\"" + validStaff.getEmail() + "\""));
    assertTrue(jwtPayload.contains("\"userId\":" + validStaff.getId()));
    assertTrue(jwtPayload.contains("\"role\":\"" + validStaff.getRole() + "\""));

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
    validRefreshToken.setUser(validStaff);
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
    assertTrue(jwtPayload.contains("\"sub\":\"" + validStaff.getEmail() + "\""));
    assertTrue(jwtPayload.contains("\"userId\":" + validStaff.getId()));
    assertTrue(jwtPayload.contains("\"role\":\"" + validStaff.getRole() + "\""));

    assertFalse(refreshToken.expirationTime().isBefore(LocalDateTime.now()));
  }

  @Test
  void refreshToken_returnsBadRequest_whenRefreshTokenExpired() throws Exception {
    RefreshToken expiredRefreshToken = new RefreshToken();
    refreshTokenRepository.save(expiredRefreshToken);
    expiredRefreshToken.setUser(validStaff);
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
            .param("email", validStaff.getEmail()))
        .andExpect(status().isOk());

    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(1, receivedMessages.length);
    assertTrue(receivedMessages[0].getSubject().contains("Password Reset Request"));
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
        .setSubject(validStaff.getEmail())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
        .compact();

    validStaff.setPasswordResetToken(validJwtToken);
    userRepository.save(validStaff);

    mockMvc.perform(get("/api/v1/auth/reset-password")
            .param("email", validStaff.getEmail()))
        .andExpect(status().isBadRequest());
  }
}