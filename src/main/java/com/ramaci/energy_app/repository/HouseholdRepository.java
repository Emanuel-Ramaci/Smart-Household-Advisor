package com.ramaci.energy_app.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import com.ramaci.energy_app.model.Household;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {
    boolean existsByName(String name);

    Optional<Household> findByName(String name);

    List<Household> findByAddress(String address);

    List<Household> findByCity(String city);

    List<Household> findByPostalCode(Integer postalCode);

    List<Household> findBySquareMeters(String squareMeters);

    List<Household> findByEnergyClass(String energyClass);

    List<Household> findByEnergyTariff(BigDecimal energyTariff);

    boolean existsByNameAndAddressAndCityAndPostalCode(String name, String address, String city, Integer postalCode);

    @Query("SELECT h FROM Household h JOIN h.members m WHERE h.id = :householdId AND m.user.id = :userId")
    Optional<Household> findAccessibleHousehold(
            @Param("householdId") Long householdId,
            @Param("userId") Long userId);

    boolean existsByNameAndIdNot(String name, Long id);
}
