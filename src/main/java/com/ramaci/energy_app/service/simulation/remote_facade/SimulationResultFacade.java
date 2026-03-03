package com.ramaci.energy_app.service.simulation.remote_facade;

import com.ramaci.energy_app.model.SimulationResult;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

public interface SimulationResultFacade {

    public List<SimulationResult> getHouseholdResults(HttpServletRequest request, Long householdId);

    public List<SimulationResult> getHouseholdResultsByType(HttpServletRequest request, Long householdId, String type);

    public void deleteResult(HttpServletRequest request, Long householdId, Long id);
}