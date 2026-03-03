package com.ramaci.energy_app.service.simulation;

import com.ramaci.energy_app.model.SimulationResult;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ISimulationResultService {
    List<SimulationResult> getSimulationResultsByHouseholdId(HttpServletRequest request, Long householdId);

    List<SimulationResult> getSimulationResultsByHouseholdIdAndSimulationType(HttpServletRequest request,
            Long householdId, String simulationType);

    void deleteSimulationResultById(HttpServletRequest request, Long householdId, Long id);
}
