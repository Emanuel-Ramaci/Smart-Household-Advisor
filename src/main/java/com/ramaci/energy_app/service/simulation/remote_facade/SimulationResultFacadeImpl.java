package com.ramaci.energy_app.service.simulation.remote_facade;

import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.service.simulation.ISimulationResultService;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class SimulationResultFacadeImpl implements SimulationResultFacade {

    private final ISimulationResultService simulationResultService;

    public SimulationResultFacadeImpl(ISimulationResultService simulationResultService) {
        this.simulationResultService = simulationResultService;
    }

    public List<SimulationResult> getHouseholdResults(HttpServletRequest request, Long householdId) {
        return simulationResultService.getSimulationResultsByHouseholdId(request, householdId);
    }

    public List<SimulationResult> getHouseholdResultsByType(HttpServletRequest request, Long householdId, String type) {
        return simulationResultService.getSimulationResultsByHouseholdIdAndSimulationType(request, householdId, type);
    }

    public void deleteResult(HttpServletRequest request, Long householdId, Long id) {
        simulationResultService.deleteSimulationResultById(request, householdId, id);
    }
}