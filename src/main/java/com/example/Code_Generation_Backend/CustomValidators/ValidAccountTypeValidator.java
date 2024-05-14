package com.example.Code_Generation_Backend.CustomValidators;

import com.example.Code_Generation_Backend.models.AccountType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidAccountTypeValidator implements ConstraintValidator<ValidAccountType, String> {

    @Override
    public boolean isValid(String stringAccountType,
        ConstraintValidatorContext constraintValidatorContext) {
      try {
        if (stringAccountType != null) {
          AccountType.valueOf(stringAccountType.toUpperCase());
          return true;
        }
        return false;
      } catch (IllegalArgumentException e) {
        return false;
      }
    }

}
