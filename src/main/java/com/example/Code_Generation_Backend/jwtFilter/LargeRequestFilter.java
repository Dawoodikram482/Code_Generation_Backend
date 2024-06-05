package com.example.Code_Generation_Backend.jwtFilter;

import jakarta.servlet.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class LargeRequestFilter implements Filter {
  @Value("25000")
  private int maxSize;
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Filter.super.init(filterConfig);
  }
  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    int messageSize = servletRequest.getContentLength();
    if (messageSize > maxSize) {
      throw new ServletException("Request size exceeds the limit");
    }
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
