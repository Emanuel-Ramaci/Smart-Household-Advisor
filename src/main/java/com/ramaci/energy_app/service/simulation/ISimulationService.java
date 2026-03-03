package com.ramaci.energy_app.service.simulation;

import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;
import com.ramaci.energy_app.model.SimulationResult;

import jakarta.servlet.http.HttpServletRequest;

public interface ISimulationService {
    SimulationResult runSimulation(HttpServletRequest request, String type,
            Long householdId,
            SimulationRequestDTO input);
}
