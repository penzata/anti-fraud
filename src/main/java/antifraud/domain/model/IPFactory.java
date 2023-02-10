package antifraud.domain.model;

public class IPFactory {
    private IPFactory() {
    }

    public static IP create(String ipAddress) {
        return IP.builder()
                .ipAddress(ipAddress)
                .build();
    }
}