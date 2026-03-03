package com.ramaci.energy_app.service;

import com.ramaci.energy_app.security.JwtUtil;
import com.ramaci.energy_app.security.TokenStore;

import jakarta.servlet.http.HttpServletRequest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.lambdaworks.crypto.SCryptUtil;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final TokenStore tokenStore;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, TokenStore tokenStore) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.tokenStore = tokenStore;
    }

    public String login(String email, String password, String loginType) {

        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Credenziali non corrette"));

        if (!SCryptUtil.check(password, user.getPassword())) {
            throw new RuntimeException("Credenziali non corrette");
        }

        // loginType può essere "USER" o "ADMIN"
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals(loginType));

        if (!hasRole) {
            throw new RuntimeException("Accesso negato: ruolo richiesto " + loginType);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles().iterator().next().getName());

        tokenStore.store(token);
        return token;
    }

    public User getAuthenticatedUser(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        // Prima cerco Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            // Poi cerco cookie 'jwt'
            if (request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if ("jwt".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        if (token == null || !tokenStore.isPresent(token)) {
            throw new RuntimeException("Token mancante o formato non valido");
        }

        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
        String email = decodedJWT.getSubject();

        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Utente non valido o disattivato"));
    }

    public void logout(HttpServletRequest request) {
        String token = null;

        // Prima cerco Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            // Poi cerco cookie 'jwt'
            if (request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if ("jwt".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        if (token == null || !tokenStore.isPresent(token)) {
            throw new RuntimeException("Token mancante o non valido");
        }

        tokenStore.remove(token);
    }

    public DecodedJWT verifyToken(String token) {
        return jwtUtil.verifyToken(token);
    }

    public boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
    }

    public void checkPermission(User user, String permissionName) {
        boolean allowed = user.getRoles().stream()
                .anyMatch(r -> r.hasPermission(permissionName));

        if (!allowed) {
            throw new RuntimeException("Accesso negato! Permesso richiesto: " + permissionName);
        }
    }
}
