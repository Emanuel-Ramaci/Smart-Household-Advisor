package com.ramaci.energy_app.security;

import java.util.Set;
import java.util.HashSet;

import org.springframework.stereotype.Component;

@Component
public class TokenStore {
    private final Set<String> validTokens = new HashSet<>();

    public void store(String token) {
        validTokens.add(token);
    }

    public boolean isPresent(String token) {
        return validTokens.contains(token);
    }

    public void remove(String token) {
        validTokens.remove(token);
    }
}
