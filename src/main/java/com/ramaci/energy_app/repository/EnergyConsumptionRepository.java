package com.ramaci.energy_app.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ramaci.energy_app.model.EnergyConsumption;
import com.ramaci.energy_app.model.Household;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EnergyConsumptionRepository extends JpaRepository<EnergyConsumption, Long> {
    List<EnergyConsumption> findByHousehold(Household household);

    List<EnergyConsumption> findByHouseholdId(Long id);

    List<EnergyConsumption> findByCategory(String category);

    List<EnergyConsumption> findByConsumptionKwh(BigDecimal consumptionKwh);

    List<EnergyConsumption> findByReferenceDate(LocalDate referenceDate);

    boolean existsByHouseholdIdAndReferenceDateAndCategory(Long householdId, LocalDate referenceDate, String category);

    void deleteByHousehold(Household household);
}
