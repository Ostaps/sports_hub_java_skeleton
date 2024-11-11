package org.softserveinc.java_be_genai_plgrnd.services.auth;

import org.softserveinc.java_be_genai_plgrnd.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

        return userRepository.findByEmail(email).
            map(
                user -> User.builder()
                    .username(email)
                    .password(user.getPassword())
                    .build()
        ).orElseThrow(
            () -> new UsernameNotFoundException("User with email [%s] not found".formatted(email))
        );
    }
}
