package antifraud.validation;

import antifraud.domain.model.enums.TransactionResult;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AvailableFeedbackValidator implements ConstraintValidator<AvailableFeedback, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            return Arrays.stream(TransactionResult.values())
                    .anyMatch(t -> t.name().equals(value));
        } catch (Exception e) {
            return false;
        }
    }
}