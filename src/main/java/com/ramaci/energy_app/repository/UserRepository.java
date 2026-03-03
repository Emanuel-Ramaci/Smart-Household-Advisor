package com.ramaci.energy_app.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.ramaci.energy_app.model.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    Optional<User> findByIdAndIsActiveTrue(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    List<User> findByFirstNameAndIsActiveTrue(String firstName);

    List<User> findByIsActiveTrue();

    List<User> findByLastNameAndIsActiveTrue(String lastName);
}
