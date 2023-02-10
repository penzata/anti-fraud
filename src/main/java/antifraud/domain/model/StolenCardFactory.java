package antifraud.domain.model;

public class StolenCardFactory {

    private StolenCardFactory() {
    }

    public static StolenCard create(String number) {
        return StolenCard.builder()
                .number(number)
                .build();
    }
}