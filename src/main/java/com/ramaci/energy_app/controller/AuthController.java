package com.ramaci.energy_app.controller;

import com.ramaci.energy_app.dto.user.LoginRequestDTO;
import com.ramaci.energy_app.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/user-login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        return performLogin(request, response, "USER");
    }

    @PostMapping("/admin-login")
    public ResponseEntity<String> loginAdmin(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        return performLogin(request, response, "ADMIN");

    }

    private ResponseEntity<String> performLogin(LoginRequestDTO request, HttpServletResponse response, String role) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword(), role);

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            cookie.setSecure(false);
            response.addCookie(cookie);

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Logout effettuato con successo");
    }
}