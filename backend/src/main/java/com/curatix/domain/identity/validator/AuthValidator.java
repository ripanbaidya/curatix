package com.curatix.domain.identity.validator;

import com.curatix.common.constant.ErrorCode;
import com.curatix.domain.identity.dto.result.PasswordRequirementResult;
import com.curatix.domain.identity.exception.UserException;
import com.curatix.domain.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;

    /**
     * Ensure that the provided email is valid in format and unique in the system.
     *
     * @param email the email to validate and check for uniqueness.
     */
    public void ensureValidAndUniqueEmail(@NonNull String email) {
        validateIfInvalidEmail(email);
        ensureEmailDoesNotExist(email);
    }

    /**
     * Validate if the provided email is in a valid format
     *
     * @param email The email to validate.
     * @throws UserException if email format is invalid.
     */
    public void validateIfInvalidEmail(@NonNull String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email).matches()) {
            throw new UserException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    /**
     * Ensure that the provided email does not already exist in the system,
     * i.e., it is not associated with any existing user.
     *
     * @param email The email to check for existence.
     * @throws UserException if the email exists (duplicate email).
     */
    public void ensureEmailDoesNotExist(@NonNull String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserException(
                    ErrorCode.DUPLICATE_EMAIL,
                    "The email address '" + email + "' is already in use."
            );
        });
    }

    /**
     * Validate the strength of the provided password against defined
     * security policies.
     *
     * @param password The password to validate.
     * @throws UserException if the password does not meet strength requirements.
     */
    public void validatePassword(
            @NonNull String password,
            @NonNull PasswordRequirementResult requirements
    ) {
        int passwordLength = password.length();
        if (passwordLength < requirements.minLength() || passwordLength > requirements.maxLength()) {
            throw new UserException(
                    ErrorCode.INVALID_PASSWORD,
                    "Password length must be between "
                            + requirements.minLength()
                            + " and "
                            + requirements.maxLength()
                            + " characters"
            );
        }
        if (requirements.requireUppercase() && !password.matches(".*[A-Z].*")) {
            throw new UserException(
                    ErrorCode.INVALID_PASSWORD,
                    "Password must contain at least one uppercase letter"
            );
        }

        if (requirements.requireLowercase() && !password.matches(".*[a-z].*")) {
            throw new UserException(
                    ErrorCode.INVALID_PASSWORD,
                    "Password must contain at least one lowercase letter"
            );
        }

        if (requirements.requireNumber() && !password.matches(".*\\d.*")) {
            throw new UserException(
                    ErrorCode.INVALID_PASSWORD,
                    "Password must contain at least one digit"
            );
        }
    }
}
