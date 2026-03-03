package com.ramaci.energy_app.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.model.Household;
import java.util.List;

@Repository
public interface SimulationResultRepository extends JpaRepository<SimulationResult, Long> {
    List<SimulationResult> findByHouseholdId(Long id);

    List<SimulationResult> findByHouseholdIdAndSimulationType(Long id, String simulationType);

    boolean existsByHouseholdIdAndSimulationType(Long householdId, String simulationType);

    boolean existsByHouseholdAndSimulationTypeAndInputData(Household household, String simulationType,
            String inputData);
}
