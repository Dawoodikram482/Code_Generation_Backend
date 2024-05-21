package com.example.Code_Generation_Backend.models.exceptions;

public class TransactionLimitException extends RuntimeException{
    public TransactionLimitException(String message) {
        super(message);
    }
}
