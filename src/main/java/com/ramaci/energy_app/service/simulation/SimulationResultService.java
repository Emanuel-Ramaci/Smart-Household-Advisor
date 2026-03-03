package com.ramaci.energy_app.service.simulation;

import org.springframework.stereotype.Service;
import com.ramaci.energy_app.repository.SimulationResultRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.ramaci.energy_app.dto.simulation.SimulationResponseDTO;
import com.ramaci.energy_app.model.SimulationResult;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimulationResultService implements ISimulationResultService {

    private final SimulationResultRepository simulationResultRepository;

    public SimulationResultService(SimulationResultRepository simulationResultRepository) {
        this.simulationResultRepository = simulationResultRepository;
    }

    public List<SimulationResponseDTO> getAllSimulationResults() {
        return simulationResultRepository.findAll().stream()
                .map(sr -> {
                    SimulationResponseDTO dto = new SimulationResponseDTO();
                    dto.setId(sr.getId());
                    dto.setSimulationType(sr.getSimulationType());
                    dto.setEstimatedSavingEuro(sr.getEstimatedSavingEuro());
                    dto.setEstimatedSavingKwh(sr.getEstimatedSavingKwh());
                    dto.setResultData(sr.getResultData());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SimulationResult> getSimulationResultsByHouseholdId(HttpServletRequest request, Long householdId) {
        return simulationResultRepository.findByHouseholdId(householdId);
    }

    @Override
    public List<SimulationResult> getSimulationResultsByHouseholdIdAndSimulationType(HttpServletRequest request,
            Long householdId,
            String simulationType) {
        return simulationResultRepository.findByHouseholdIdAndSimulationType(householdId, simulationType);
    }

    @Override
    public void deleteSimulationResultById(HttpServletRequest request, Long householdId, Long id) {
        simulationResultRepository.deleteById(id);
    }
}