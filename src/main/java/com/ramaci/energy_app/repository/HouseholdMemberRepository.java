package com.ramaci.energy_app.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.ramaci.energy_app.model.HouseholdMember;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.model.User;
import java.util.List;

@Repository
public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, Long> {
    Optional<HouseholdMember> findByHousehold(Household household);

    List<HouseholdMember> findByUserId(Long id);

    List<HouseholdMember> findByHouseholdId(Long id);

    Optional<HouseholdMember> findByUser(User user);

    Optional<HouseholdMember> findByMembershipType(String membershipType);

    Optional<HouseholdMember> findByHouseholdAndUser(Household household, User user);

    boolean existsByHouseholdIdAndUserId(Long householdId, Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByHouseholdAndUser(Household household, User user);

    void deleteByHousehold(Household household);
}
