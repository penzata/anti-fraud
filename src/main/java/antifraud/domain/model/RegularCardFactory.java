package antifraud.domain.model;

public class RegularCardFactory {

    private RegularCardFactory() {
    }

    public static RegularCard create(String number) {
        return RegularCard.builder()
                .number(number)
                .build();
    }
}