package com.ramaci.energy_app.dto.user;

import com.ramaci.energy_app.model.Role;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminUserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private String roleNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public void setRolesFromSet(Set<Role> roles) {
        if (roles != null) {
            this.roleNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", "));
        } else {
            this.roleNames = "";
        }
    }

}
