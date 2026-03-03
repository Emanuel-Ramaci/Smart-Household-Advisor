package com.ramaci.energy_app.service.simulation.strategy;

import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;

public interface SimulationStrategy {
    SimulationResult simulate(Household household, SimulationRequestDTO input);
}