package com.example.Code_Generation_Backend.jwtFilter;

import com.example.Code_Generation_Backend.security.JwtProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
  private final JwtProvider jwtTokenProvider;
  public JwtTokenFilter(JwtProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = getToken(request);
    if (token != null) {
      try {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
      }
      catch (JwtException e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid JWT token");
        response.getWriter().flush();
        return;
      }
      catch (Exception exception){
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter()
            .write("Something went wrong while validating the JWT. Please try again later.");
        response.getWriter().flush();
        return;
      }
    }
    filterChain.doFilter(request, response);
  }
  private String getToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      return null;
    }
    return header.substring(7);
  }
}
