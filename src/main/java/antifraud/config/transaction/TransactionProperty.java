package antifraud.config.transaction;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Slf4j
@ConfigurationProperties(prefix = "transaction.values")
@Validated
public record TransactionProperty(@Positive
                                  long allowed,
                                  @Positive
                                  long manualProcessing,
                                  @Positive
                                  long correlation,
                                  @Positive
                                  double currentLimitFactor,
                                  @Positive
                                  double currentDepositFactor) {

    @Bean
    CommandLineRunner runner(TransactionProperty transactionProperty) {
        return args -> {
            log.info("ALLOWED value limit is {}", transactionProperty.allowed());
            log.info("MANUAL_PROCESSING value limit is {}", transactionProperty.manualProcessing());
            log.info("Correlation value based on unique IPs or regions is {}", transactionProperty.correlation());
            log.info("Coefficient for current transaction limit is {}", transactionProperty.currentLimitFactor());
            log.info("Coefficient for deposit is {}", transactionProperty.currentDepositFactor());
        };
    }
}