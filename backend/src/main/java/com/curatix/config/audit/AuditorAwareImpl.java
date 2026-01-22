package com.curatix.config.audit;

import com.curatix.domain.identity.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * We will use the user Email as the auditor identifier.
 */
@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<@NonNull String> {

    @Override
    public @NonNull Optional<String> getCurrentAuditor() {
        final Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken
        ) {
            return Optional.empty();
        }

        Object principle = authentication.getPrincipal();
        if (principle instanceof User user) {
            return Optional.ofNullable(user.getEmail());
        }

        return Optional.of("SYSTEM");
    }
}
