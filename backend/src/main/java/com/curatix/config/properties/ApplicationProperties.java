package com.curatix.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application specific properties
 */
@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        String name,
        String version,
        String logo,
        String buildNumber,
        String copyright,
        License license,
        Support support,
        Socials socials,
        Developer developer
) {

    /**
     * License
     */
    public record License(
            String name,
            String url
    ) {
    }

    /**
     * Support info
     */
    public record Support(
            String email,
            String workingHours
    ) {
    }

    /**
     * Social links
     */
    public record Socials(
            String github,
            String twitter,
            String instagram
    ) {
    }

    /**
     * Developer
     */
    public record Developer(
            String name,
            String email
    ) {
    }
}
