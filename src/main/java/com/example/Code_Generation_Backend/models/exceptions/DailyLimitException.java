package com.example.Code_Generation_Backend.models.exceptions;

public class DailyLimitException extends RuntimeException{
  public DailyLimitException(String message){
     super(message);
  }
}
