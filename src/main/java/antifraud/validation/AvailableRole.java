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
 * Checks the input value if it's available as UserRole Enum.
 * Accepts String.
 */
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = AvailableRoleValidator.class)
@Documented
public @interface AvailableRole {

    String message() default "{antifraud.validation.AvailableRole.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}