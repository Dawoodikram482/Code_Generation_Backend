package com.example.Code_Generation_Backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
  private User user;

  @BeforeEach
  void setUp() {
    user = User.builder().build();
  }

  @Test
  void userBuilderShouldResultInValidUserObject() {
    assertNotNull(user);
  }
  @Test
  void setDayLimitToNegativeNumberReturnsPositiveNumber() {
    user.setDayLimit(-1);
    assertEquals(0, user.getDayLimit());
  }
  @Test
  void setDayLimitToPositiveNumberReturnsPositiveNumber() {
    user.setDayLimit(1);
    assertEquals(1, user.getDayLimit());
  }
  @Test
  void setTransactionLimitToNegativeNumberReturnsPositiveNumber() {
    user.setTransactionLimit(-1);
    assertEquals(0, user.getTransactionLimit());
  }
  @Test
  void getFullNameReturnsFullName() {
    user.setFirstName("John");
    user.setLastName("Doe");
    assertEquals("John Doe", user.getFullName());
  }
  @Test
  void isApprovedReturnsIsApproved() {
    user.setIsApproved(true);
    assertTrue(user.getIsApproved());
  }
}