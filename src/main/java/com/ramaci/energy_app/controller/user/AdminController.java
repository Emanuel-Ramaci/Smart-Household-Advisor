package com.ramaci.energy_app.controller.user;

import org.springframework.web.bind.annotation.RestController;

import com.ramaci.energy_app.dto.user.AdminUserRequestDTO;
import com.ramaci.energy_app.dto.user.AdminUserResponseDTO;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.service.user.AdminService;
import com.ramaci.energy_app.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final AdminService adminUserService;
    private final AuthService authService;

    public AdminController(AdminService adminUserService,
            AuthService authService) {
        this.adminUserService = adminUserService;
        this.authService = authService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @RequestBody AdminUserRequestDTO dto,
            HttpServletRequest request) {

        checkAdminPermission(request, "ADMIN_CREATE_USER");

        try {
            User createdUser = adminUserService.createUser(dto);

            AdminUserResponseDTO response = new AdminUserResponseDTO();
            response.setId(createdUser.getId());
            response.setEmail(createdUser.getEmail());
            response.setFirstName(createdUser.getFirstName());
            response.setLastName(createdUser.getLastName());
            response.setActive(createdUser.isActive());
            response.setRolesFromSet(createdUser.getRoles());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Errore durante la creazione dell'utente: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponseDTO>> getAllUsers(HttpServletRequest request) {

        checkAdminPermission(request, "ADMIN_READ_ALL_USERS");

        List<AdminUserResponseDTO> users = adminUserService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    // Soft delete di un utente
    @PutMapping("/{id}")
    public ResponseEntity<Void> activateDeactivateUser(@PathVariable Long id, HttpServletRequest request) {

        checkAdminPermission(request, "ADMIN_ACTIVATE_DEACTIVATE_USER");

        try {
            adminUserService.activateDeactivateUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Hard delete di un utente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {

        checkAdminPermission(request, "ADMIN_DELETE_USER");

        try {
            adminUserService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private void checkAdminPermission(HttpServletRequest request, String permission) {
        User loggedUser = authService.getAuthenticatedUser(request);
        if (!authService.isAdmin(loggedUser)) {
            throw new RuntimeException("Operazione non consentita: ruolo ADMIN richiesto");
        }
        authService.checkPermission(loggedUser, permission);
    }
}
