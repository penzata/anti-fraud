package antifraud.validation;

import antifraud.domain.model.enums.WorldRegion;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AvailableRegionValidator implements ConstraintValidator<AvailableRegion, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            return Arrays.stream(WorldRegion.values())
                    .anyMatch(r -> r.name().equals(value));
        } catch (Exception ex) {
            return false;
        }
    }
}