package antifraud.validation;

import antifraud.domain.model.enums.UserRole;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AvailableRoleValidator implements ConstraintValidator<AvailableRole, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            return Arrays.stream(UserRole.values())
                .anyMatch(r -> r.name().equals(value));
        } catch (Exception e) {
            return false;
        }
    }
}