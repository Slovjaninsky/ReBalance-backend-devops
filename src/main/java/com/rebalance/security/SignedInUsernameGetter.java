package com.rebalance.security;

import com.rebalance.entity.User;
import com.rebalance.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SignedInUsernameGetter {
    private final UserRepository userRepository;

    public User getUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = jwt.getClaim("email");
        return userRepository.findByEmail(email).get();
    }
}
