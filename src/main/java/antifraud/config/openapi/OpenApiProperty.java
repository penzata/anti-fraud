package antifraud.config.openapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openapi")
public record OpenApiProperty(String projectTitle,
                              String projectDescription,
                              String projectVersion) {
}