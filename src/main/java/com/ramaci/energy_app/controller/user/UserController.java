package com.ramaci.energy_app.controller.user;

import org.springframework.web.bind.annotation.RestController;

import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.service.user.UserService;
import com.ramaci.energy_app.service.AuthService;
import com.ramaci.energy_app.dto.user.PasswordRequestDTO;
import com.ramaci.energy_app.dto.user.UserProfileResponseDTO;
import com.ramaci.energy_app.dto.user.UserRequestDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/me")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService,
            AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRequestDTO request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User createdUser = userService.registerUser(user);

        return ResponseEntity.ok(createdUser);
    }

    @GetMapping
    public ResponseEntity<UserProfileResponseDTO> getMyProfile(HttpServletRequest request) {
        User loggedUser = authService.getAuthenticatedUser(request);
        authService.checkPermission(loggedUser, "USER_READ_OWN_PROFILE");
        User user = userService.getMyProfile(loggedUser.getId());

        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<UserProfileResponseDTO> updateMyProfile(HttpServletRequest request,
            @RequestBody UserRequestDTO requestDTO) {

        User loggedUser = authService.getAuthenticatedUser(request);
        authService.checkPermission(loggedUser, "USER_UPDATE_OWN_PROFILE");
        User updated = new User();
        updated.setEmail(requestDTO.getEmail());
        updated.setFirstName(requestDTO.getFirstName());
        updated.setLastName(requestDTO.getLastName());

        User savedUser = userService.updateMyProfile(loggedUser.getId(), updated);

        UserProfileResponseDTO response = new UserProfileResponseDTO();
        response.setEmail(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<User> changePassword(HttpServletRequest request,
            @RequestBody PasswordRequestDTO dto) {

        User loggedUser = authService.getAuthenticatedUser(request);
        authService.checkPermission(loggedUser, "USER_CHANGE_OWN_PASSWORD");
        userService.changePassword(loggedUser.getId(), dto.getNewPassword());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateMyAccount(HttpServletRequest request) {
        User loggedUser = authService.getAuthenticatedUser(request);
        authService.checkPermission(loggedUser, "USER_DEACTIVATE_OWN_ACCOUNT");

        try {
            userService.deactivateMyAccount(loggedUser.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}