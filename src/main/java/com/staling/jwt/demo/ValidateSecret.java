package com.staling.jwt.demo;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import io.jsonwebtoken.SignatureAlgorithm;
import static io.jsonwebtoken.SignatureAlgorithm.*;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateSecretImpl.class)
@Documented
public @interface ValidateSecret {
    enum Type { SHARED, ASYMMETRIC }
    Type type();
    
    String message() default "Signature algorithm must be of matching type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class ValidateSecretImpl implements ConstraintValidator<ValidateSecret, String> {
    private static Map<ValidateSecret.Type, Set<SignatureAlgorithm>> algorithms = new EnumMap<>(ValidateSecret.Type.class);
    static {
        algorithms.put(ValidateSecret.Type.SHARED, new HashSet<>(Arrays.asList(HS256, HS384, HS512)));
        algorithms.put(ValidateSecret.Type.ASYMMETRIC, new HashSet<>(Arrays.asList(RS256, RS384, RS512, PS256, PS384, PS512, ES256, ES384, ES512)));
    }
    private ValidateSecret.Type type;

    @Override
    public void initialize(ValidateSecret constraintAnnotation) {
        type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            return algorithms.get(type).contains(SignatureAlgorithm.valueOf(value));
        } catch (Exception e) {
            return false;
        }
    }

}
