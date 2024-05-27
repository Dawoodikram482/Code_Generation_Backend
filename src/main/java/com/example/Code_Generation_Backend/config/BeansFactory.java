package com.example.Code_Generation_Backend.config;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.Random;
@EnableWebSecurity
@Configuration
public class BeansFactory {
  @Bean
  public Random random() {
    return new Random();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers("/h2-console/**").permitAll()  // Allow access to H2 console
            .requestMatchers("/transactions/**").permitAll()
            .requestMatchers("/users/{userId}/approve").hasRole("EMPLOYEE")
            .requestMatchers("/test-employee-role").hasRole("EMPLOYEE")
            .anyRequest().authenticated()  // Require authentication for all other requests
        )
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**", "/transactions/**")  // Disable CSRF protection for H2 console
        )
        .headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)  // Allow H2 console to be embedded in a frame
        );

    return http.build();
  }
  @Bean
  public EntityManager entityManager(){
    return new EntityManager() {
      @Override
      public void persist(Object o) {

      }

      @Override
      public <T> T merge(T t) {
        return null;
      }

      @Override
      public void remove(Object o) {

      }

      @Override
      public <T> T find(Class<T> aClass, Object o) {
        return null;
      }

      @Override
      public <T> T find(Class<T> aClass, Object o, Map<String, Object> map) {
        return null;
      }

      @Override
      public <T> T find(Class<T> aClass, Object o, LockModeType lockModeType) {
        return null;
      }

      @Override
      public <T> T find(Class<T> aClass, Object o, LockModeType lockModeType, Map<String, Object> map) {
        return null;
      }

      @Override
      public <T> T getReference(Class<T> aClass, Object o) {
        return null;
      }

      @Override
      public void flush() {

      }

      @Override
      public void setFlushMode(FlushModeType flushModeType) {

      }

      @Override
      public FlushModeType getFlushMode() {
        return null;
      }

      @Override
      public void lock(Object o, LockModeType lockModeType) {

      }

      @Override
      public void lock(Object o, LockModeType lockModeType, Map<String, Object> map) {

      }

      @Override
      public void refresh(Object o) {

      }

      @Override
      public void refresh(Object o, Map<String, Object> map) {

      }

      @Override
      public void refresh(Object o, LockModeType lockModeType) {

      }

      @Override
      public void refresh(Object o, LockModeType lockModeType, Map<String, Object> map) {

      }

      @Override
      public void clear() {

      }

      @Override
      public void detach(Object o) {

      }

      @Override
      public boolean contains(Object o) {
        return false;
      }

      @Override
      public LockModeType getLockMode(Object o) {
        return null;
      }

      @Override
      public void setProperty(String s, Object o) {

      }

      @Override
      public Map<String, Object> getProperties() {
        return Map.of();
      }

      @Override
      public Query createQuery(String s) {
        return null;
      }

      @Override
      public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return null;
      }

      @Override
      public Query createQuery(CriteriaUpdate criteriaUpdate) {
        return null;
      }

      @Override
      public Query createQuery(CriteriaDelete criteriaDelete) {
        return null;
      }

      @Override
      public <T> TypedQuery<T> createQuery(String s, Class<T> aClass) {
        return null;
      }

      @Override
      public Query createNamedQuery(String s) {
        return null;
      }

      @Override
      public <T> TypedQuery<T> createNamedQuery(String s, Class<T> aClass) {
        return null;
      }

      @Override
      public Query createNativeQuery(String s) {
        return null;
      }

      @Override
      public Query createNativeQuery(String s, Class aClass) {
        return null;
      }

      @Override
      public Query createNativeQuery(String s, String s1) {
        return null;
      }

      @Override
      public StoredProcedureQuery createNamedStoredProcedureQuery(String s) {
        return null;
      }

      @Override
      public StoredProcedureQuery createStoredProcedureQuery(String s) {
        return null;
      }

      @Override
      public StoredProcedureQuery createStoredProcedureQuery(String s, Class... classes) {
        return null;
      }

      @Override
      public StoredProcedureQuery createStoredProcedureQuery(String s, String... strings) {
        return null;
      }

      @Override
      public void joinTransaction() {

      }

      @Override
      public boolean isJoinedToTransaction() {
        return false;
      }

      @Override
      public <T> T unwrap(Class<T> aClass) {
        return null;
      }

      @Override
      public Object getDelegate() {
        return null;
      }

      @Override
      public void close() {

      }

      @Override
      public boolean isOpen() {
        return false;
      }

      @Override
      public EntityTransaction getTransaction() {
        return null;
      }

      @Override
      public EntityManagerFactory getEntityManagerFactory() {
        return null;
      }

      @Override
      public CriteriaBuilder getCriteriaBuilder() {
        return null;
      }

      @Override
      public Metamodel getMetamodel() {
        return null;
      }

      @Override
      public <T> EntityGraph<T> createEntityGraph(Class<T> aClass) {
        return null;
      }

      @Override
      public EntityGraph<?> createEntityGraph(String s) {
        return null;
      }

      @Override
      public EntityGraph<?> getEntityGraph(String s) {
        return null;
      }

      @Override
      public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> aClass) {
        return List.of();
      }
    };
  }
}
