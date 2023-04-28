package com.greet.api.util;

import com.greet.api.dto.SignUpRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidatorUtils implements ConstraintValidator<PasswordMatchesUtils, SignUpRequest> {

    @Override
    public boolean isValid(final SignUpRequest user, final ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getMatchingPassword());
    }

}
