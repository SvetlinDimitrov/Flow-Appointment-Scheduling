package com.internship.flow_appointment_scheduling.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private final UserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException {

    String authorizationHeader = request.getHeader("Authorization");

    String email = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
    }

    try {

      if (jwt != null && !jwtUtil.isTokenExpired(jwt)) {
        email = jwtUtil.extractEmail(jwt);
      }

      if (email != null) {

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken
            (userDetails, null, userDetails.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
      chain.doFilter(request, response);

    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(e.getMessage());
    }
  }
}