package antifraud.domain.model;

public class IPFactory {
    private IPFactory() {
    }

    public static IP create(String ipAddress) {
        return IP.builder()
                .ipAddress(ipAddress)
                .build();
    }

    public static IP createWithId(Long id, String ipAddress) {
        return IP.builder()
                .id(id)
                .ipAddress(ipAddress)
                .build();
    }
}