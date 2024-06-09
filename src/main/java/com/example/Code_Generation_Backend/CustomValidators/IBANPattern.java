package com.example.Code_Generation_Backend.CustomValidators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD, ElementType.ANNOTATION_TYPE,
  ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = {})
@Pattern(regexp = "NL\\d{2}UNIB{4}\\d{10}", message = "Not a valid IBAN")
public @interface IBANPattern {
  String message() default "Invalid IBAN";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
