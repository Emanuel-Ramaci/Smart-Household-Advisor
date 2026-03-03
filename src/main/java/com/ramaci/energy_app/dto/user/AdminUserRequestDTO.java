package com.ramaci.energy_app.dto.user;

import java.util.Set;
import com.ramaci.energy_app.model.Role;
import java.util.stream.Collectors;

public class AdminUserRequestDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Set<String> roleNames;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<String> roleNames) {
        this.roleNames = roleNames;
    }

    public void setRoles(Set<Role> roles) {
        this.roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}