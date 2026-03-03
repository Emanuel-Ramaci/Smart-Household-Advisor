package com.ramaci.energy_app.controller.simulation;

import jakarta.servlet.http.HttpServletRequest;

import com.ramaci.energy_app.dto.simulation.SimulationResponseDTO;
import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.service.simulation.remote_facade.SimulationResultFacade;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation-results")
public class SimulationResultController {

    private final SimulationResultFacade simulationResultFacade;

    public SimulationResultController(SimulationResultFacade simulationResultFacade) {
        this.simulationResultFacade = simulationResultFacade;
    }

    @GetMapping("/{householdId}")
    public ResponseEntity<List<SimulationResponseDTO>> getResults(HttpServletRequest request,
            @PathVariable Long householdId) {
        List<SimulationResult> results = simulationResultFacade.getHouseholdResults(request, householdId);

        List<SimulationResponseDTO> dto = mapToDTO(results);

        return ResponseEntity.ok(dto);
    }

    // GET risultati filtrati per tipo
    @GetMapping("/{householdId}/{type}")
    public ResponseEntity<List<SimulationResponseDTO>> getResultsByType(
            HttpServletRequest request,
            @PathVariable Long householdId,
            @PathVariable String type) {

        List<SimulationResult> results = simulationResultFacade.getHouseholdResultsByType(request, householdId, type);
        List<SimulationResponseDTO> dto = mapToDTO(results);

        return ResponseEntity.ok(dto);
    }

    // DELETE risultato
    @DeleteMapping("/{householdId}/{resultId}")
    public ResponseEntity<Void> deleteResult(
            HttpServletRequest request,
            @PathVariable Long householdId,
            @PathVariable("resultId") Long id) {

        simulationResultFacade.deleteResult(request, householdId, id);
        return ResponseEntity.noContent().build();
    }

    private List<SimulationResponseDTO> mapToDTO(List<SimulationResult> results) {
        List<SimulationResponseDTO> dto = results.stream()
                .map(r -> new SimulationResponseDTO(
                        r.getId(),
                        r.getSimulationType(),
                        r.getEstimatedSavingEuro(),
                        r.getEstimatedSavingKwh(),
                        r.getResultData()))
                .toList();
        return dto;
    }
}