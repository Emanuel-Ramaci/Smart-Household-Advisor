package com.ramaci.energy_app.service.simulation;

import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;
import com.ramaci.energy_app.repository.SimulationResultRepository;
import com.ramaci.energy_app.service.simulation.strategy.HeatingReductionSimulation;
import com.ramaci.energy_app.service.simulation.strategy.SimulationStrategy;
import com.ramaci.energy_app.service.simulation.strategy.StandByEliminationSimulation;
import com.ramaci.energy_app.service.simulation.strategy.TimeShiftSimulation;
import com.ramaci.energy_app.repository.HouseholdRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

@Service
public class SimulationService implements ISimulationService {

    private final SimulationResultRepository simulationResultRepository;
    private final HouseholdRepository householdRepository;

    public SimulationService(SimulationResultRepository simulationResultRepository,
            HouseholdRepository householdRepository) {
        this.simulationResultRepository = simulationResultRepository;
        this.householdRepository = householdRepository;
    }

    private SimulationStrategy resolveStrategy(String type) {
        return switch (type) {
            case "HEATING" -> new HeatingReductionSimulation();
            case "STANDBY" -> new StandByEliminationSimulation();
            case "TIMESHIFT" -> new TimeShiftSimulation();
            default -> throw new IllegalArgumentException("Tipo simulazione non valido");
        };
    }

    @Transactional
    public SimulationResult runSimulation(HttpServletRequest request, String type,
            Long householdId,
            SimulationRequestDTO input) {
        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + householdId));

        SimulationStrategy strategy = resolveStrategy(type);

        SimulationResult result = strategy.simulate(household, input);

        return simulationResultRepository.save(result);
    }
}