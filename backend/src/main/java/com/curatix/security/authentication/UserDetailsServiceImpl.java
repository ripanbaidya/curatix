package com.curatix.security.authentication;

import com.curatix.domain.identity.entity.User;
import com.curatix.domain.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link UserDetailsService}
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load User by his Username (Email)
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userDetails", key = "#email", unless = "#result == null")
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        log.debug("Loading user details for email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found for email" + email)
        );

        log.debug("Successfully loaded user- ID: {}, Email: {}", user.getId(), user.getEmail());
        return user;
    }
}
