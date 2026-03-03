package com.ramaci.energy_app.service.user;

import org.springframework.stereotype.Service;
import com.ramaci.energy_app.repository.UserRepository;
import com.ramaci.energy_app.service.AuthService;

import com.ramaci.energy_app.repository.RoleRepository;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.dto.user.AdminUserRequestDTO;
import com.ramaci.energy_app.dto.user.AdminUserResponseDTO;
import com.ramaci.energy_app.model.Role;
import com.lambdaworks.crypto.SCryptUtil;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthService authService;

    public AdminService(UserRepository userRepository,
            RoleRepository roleRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authService = authService;
    }

    public User createUser(AdminUserRequestDTO dto) {
        // Controllo se l'email esiste già
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email già registrata");
        }

        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        newUser.setActive(true);
        newUser.setCreatedAt(new Date());

        // Hash della password
        newUser.setPassword(SCryptUtil.scrypt(dto.getPassword(), 32768, 8, 1));

        // Assegna i ruoli se specificati
        assignRoles(newUser, dto.getRoleNames());

        return userRepository.save(newUser);
    }

    private void assignRoles(User user, Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();

        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Ruolo non trovato: " + roleName));
            roles.add(role);
        }

        user.setRoles(roles);
    }

    public List<AdminUserResponseDTO> getAllUsers() {

        return userRepository.findAll().stream()
                .map(user -> {
                    AdminUserResponseDTO dto = new AdminUserResponseDTO();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setRolesFromSet(user.getRoles());
                    dto.setActive(user.isActive());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<User> getUsersByFirstName(String firstName) {
        return userRepository.findByFirstNameAndIsActiveTrue(firstName);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    public User updateUser(User user) {

        User existingUser = getUserById(user.getId());

        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setRoles(user.getRoles());
        existingUser.setActive(user.isActive());
        existingUser.setUpdatedAt(new Date());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(SCryptUtil.scrypt(user.getPassword(), 32768, 8, 1));
        }

        return userRepository.save(existingUser);
    }

    public void activateDeactivateUser(Long id) {
        User user = getUserById(id);
        if (authService.isAdmin(user)) {
            throw new RuntimeException("Un ADMIN non può disattivare l'account di un'altro ADMIN");
        }

        boolean a = user.isActive();
        if (a) {
            user.setActive(false);
        } else {
            user.setActive(true);
        }

        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        if (authService.isAdmin(user)) {
            throw new RuntimeException("Un ADMIN non può eliminare l'account di un'altro ADMIN");
        }

        userRepository.delete(user);
    }
}
