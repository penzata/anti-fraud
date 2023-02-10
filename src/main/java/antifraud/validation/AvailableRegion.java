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
 * Checks the input value if it's available as WorldRegion Enum.
 * Accepts String.
 */
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = AvailableRegionValidator.class)
@Documented
public @interface AvailableRegion {

    String message() default "{antifraud.validation.AvailableRegion.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}