package com.example.Code_Generation_Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class BeansFactory {
  @Bean
  public Random random() {
    return new Random();
  }
}
