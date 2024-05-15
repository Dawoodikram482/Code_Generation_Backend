package com.example.Code_Generation_Backend.CustomValidators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidAccountTypeValidator.class)
public @interface ValidAccountType {
  String message() default "The account type is not valid";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
