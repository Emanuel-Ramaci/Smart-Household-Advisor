package com.ramaci.energy_app.service.simulation.remote_facade;

import com.ramaci.energy_app.dto.simulation.SimulationResponseDTO;
import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface SimulationFacade {

    SimulationResponseDTO runHeatingSimulation(HttpServletRequest request, Long householdId,
            SimulationRequestDTO input);

    SimulationResponseDTO runStandBySimulation(HttpServletRequest request, Long householdId,
            SimulationRequestDTO input);

    SimulationResponseDTO runTimeShiftSimulation(HttpServletRequest request, Long householdId,
            SimulationRequestDTO input);
}