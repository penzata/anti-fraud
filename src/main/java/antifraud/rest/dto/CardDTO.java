package antifraud.rest.dto;

import antifraud.domain.model.StolenCard;
import antifraud.domain.model.StolenCardFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.hibernate.validator.constraints.CreditCardNumber;

import jakarta.validation.constraints.NotBlank;

@Builder
public record CardDTO(@JsonProperty(access = JsonProperty.Access.READ_ONLY)
                      Long id,
                      @NotBlank
                      @CreditCardNumber
                      String number) {
    public static CardDTO fromModel(StolenCard storedCard) {
        return CardDTO.builder()
                .id(storedCard.getId())
                .number(storedCard.getNumber())
                .build();
    }

    public StolenCard toModel() {
        return StolenCardFactory.create(number);
    }
}