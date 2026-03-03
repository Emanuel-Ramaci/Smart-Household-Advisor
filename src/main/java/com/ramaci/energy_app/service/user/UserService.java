package com.ramaci.energy_app.service.user;

import org.springframework.stereotype.Service;
import com.ramaci.energy_app.repository.UserRepository;
import com.ramaci.energy_app.repository.RoleRepository;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.model.Role;
import com.lambdaworks.crypto.SCryptUtil;
import java.util.Date;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Registrazione utente
    public User registerUser(User user) {

        if (userRepository.findByEmailAndIsActiveTrue(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email già registrata");
        }

        user.setPassword(SCryptUtil.scrypt(user.getPassword(), 32768, 8, 1));

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Ruolo USER non trovato"));

        user.getRoles().add(defaultRole);
        user.setCreatedAt(new Date());
        user.setActive(true);

        return userRepository.save(user);
    }

    // Visualizzazione del profilo personale
    public User getMyProfile(Long loggedUserId) {
        return userRepository.findByIdAndIsActiveTrue(loggedUserId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    public User updateMyProfile(Long loggedUserId, User updatedData) {

        User existingUser = getMyProfile(loggedUserId);

        if (!existingUser.getEmail().equals(updatedData.getEmail())
                && userRepository.findByEmailAndIsActiveTrue(updatedData.getEmail()).isPresent()) {
            throw new RuntimeException("Email già registrata");
        }

        existingUser.setEmail(updatedData.getEmail());
        existingUser.setFirstName(updatedData.getFirstName());
        existingUser.setLastName(updatedData.getLastName());
        existingUser.setUpdatedAt(new Date());

        return userRepository.save(existingUser);
    }

    public void changePassword(Long loggedUserId, String newPassword) {

        User user = getMyProfile(loggedUserId);

        user.setPassword(SCryptUtil.scrypt(newPassword, 32768, 8, 1));
        user.setUpdatedAt(new Date());

        userRepository.save(user);
    }

    // Disattivazione del proprio profilo
    public void deactivateMyAccount(Long loggedUserId) {

        User user = getMyProfile(loggedUserId);
        user.setActive(false);
        user.setUpdatedAt(new Date());

        userRepository.save(user);
    }
}