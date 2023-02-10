package antifraud.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Checks the input value if it's available as TransactionResult Enum.
 * Accepts String.
 */
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = AvailableFeedbackValidator.class)
@Documented
public @interface AvailableFeedback {

    String message() default "{antifraud.validation.AvailableFeedback.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
