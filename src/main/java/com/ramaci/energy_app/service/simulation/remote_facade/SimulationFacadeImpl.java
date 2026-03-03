package com.ramaci.energy_app.service.simulation.remote_facade;

import com.ramaci.energy_app.dto.simulation.SimulationResponseDTO;
import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.service.simulation.ISimulationService;
import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public class SimulationFacadeImpl implements SimulationFacade {

    private final ISimulationService simulationService;

    public SimulationFacadeImpl(ISimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    public SimulationResponseDTO runHeatingSimulation(HttpServletRequest request, Long householdId,
            SimulationRequestDTO input) {

        SimulationResult result = simulationService
                .runSimulation(request, "HEATING", householdId, input);

        return mapToDTO(result);
    }

    @Override
    public SimulationResponseDTO runStandBySimulation(HttpServletRequest request, Long householdId,
            SimulationRequestDTO input) {

        SimulationResult result = simulationService
                .runSimulation(request, "STANDBY", householdId, input);

        return mapToDTO(result);
    }

    @Override
    public SimulationResponseDTO runTimeShiftSimulation(HttpServletRequest request, Long householdId,
            SimulationRequestDTO input) {

        SimulationResult result = simulationService
                .runSimulation(request, "TIMESHIFT", householdId, input);

        return mapToDTO(result);
    }

    private SimulationResponseDTO mapToDTO(SimulationResult result) {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setSimulationType(result.getSimulationType());
        dto.setEstimatedSavingEuro(result.getEstimatedSavingEuro());
        dto.setEstimatedSavingKwh(result.getEstimatedSavingKwh());
        dto.setResultData(result.getResultData());
        return dto;
    }
}