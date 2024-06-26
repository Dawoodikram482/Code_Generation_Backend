package com.example.Code_Generation_Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
    http

            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/register").permitAll()
                    .requestMatchers("/users/register").permitAll()
                    .requestMatchers("/users/**").permitAll()
                    .requestMatchers("/accounts/**").permitAll()
                    .requestMatchers("/atm/**").permitAll()
                    .requestMatchers("/accounts/search-iban").permitAll()
                    .anyRequest().authenticated());

    return http.build();
  }
}
