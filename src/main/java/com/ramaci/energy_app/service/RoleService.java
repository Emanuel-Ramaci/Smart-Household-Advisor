package com.ramaci.energy_app.service;

import org.springframework.stereotype.Service;
import com.ramaci.energy_app.repository.RoleRepository;
import com.ramaci.energy_app.model.Role;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new RuntimeException("Ruolo già registrato");
        }
        return roleRepository.save(role);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato con nome: " + name));
    }

    public Role updateRole(Role role) {
        Role existingRole = roleRepository.findById(role.getId())
                .orElseThrow(() -> new RuntimeException("Ruolo non trovato con ID: " + role.getId()));

        existingRole.setName(role.getName());

        return roleRepository.save(existingRole);
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Ruolo non trovato con ID: " + id);
        }
        roleRepository.deleteById(id);
    }
}
