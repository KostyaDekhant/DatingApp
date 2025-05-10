package com.datingapp.datingapp.security;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

@Service  // <- важно!
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User entity = userRepo.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("No user: " + login));
        UserBuilder builder = org.springframework.security.core.userdetails.User
                .withUsername(entity.getLogin());
        builder.password(entity.getPassword());
        // при необходимости добавить роли/authorities:
        builder.roles("USER");
        return builder.build();
    }
}
