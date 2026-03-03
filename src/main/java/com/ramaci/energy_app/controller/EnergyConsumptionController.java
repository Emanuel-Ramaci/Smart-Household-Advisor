package com.ramaci.energy_app.controller;

import com.ramaci.energy_app.dto.energy_consumption.EnergyConsumptionRequestDTO;
import com.ramaci.energy_app.dto.energy_consumption.EnergyConsumptionResponseDTO;
import com.ramaci.energy_app.model.EnergyConsumption;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.service.AuthService;
import com.ramaci.energy_app.service.EnergyConsumptionService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/energy")
public class EnergyConsumptionController {

    private final EnergyConsumptionService energyConsumptionService;
    private final AuthService authService;

    public EnergyConsumptionController(EnergyConsumptionService energyConsumptionService,
            AuthService authService) {
        this.energyConsumptionService = energyConsumptionService;
        this.authService = authService;
    }

    // Creazione di un consumo energetico per una specifica casa
    @PostMapping("/household/{householdId}")
    public ResponseEntity<EnergyConsumptionResponseDTO> createEnergyConsumption(
            HttpServletRequest request,
            @PathVariable Long householdId,
            @RequestBody EnergyConsumptionRequestDTO dto) {

        User user = authService.getAuthenticatedUser(request);
        EnergyConsumption ec = new EnergyConsumption();
        ec.setCategory(dto.getCategory());
        ec.setConsumptionKwh(dto.getConsumptionKwh());
        ec.setReferenceDate(dto.getReferenceDate());

        EnergyConsumption createdEc = energyConsumptionService.createEnergyConsumption(ec,
                householdId, user);

        EnergyConsumptionResponseDTO response = mapToDTO(createdEc);

        return ResponseEntity.ok(response);
    }

    // Accesso ad uno specifico consumo
    @GetMapping("/{id}")
    public ResponseEntity<EnergyConsumptionResponseDTO> getEnergyConsumption(
            HttpServletRequest request,
            @PathVariable Long id) {

        User user = authService.getAuthenticatedUser(request);
        EnergyConsumption ec = energyConsumptionService.getEnergyConsumptionById(id, user);

        return ResponseEntity.ok(mapToDTO(ec));
    }

    // Accesso a tutti i consumi relativi ad una specifica casa
    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<EnergyConsumptionResponseDTO>> getAllByHousehold(
            HttpServletRequest request,
            @PathVariable Long householdId) {

        User user = authService.getAuthenticatedUser(request);
        List<EnergyConsumption> list = energyConsumptionService.getAllByHousehold(householdId, user);

        List<EnergyConsumptionResponseDTO> responseList = list.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnergyConsumptionResponseDTO> updateEnergyConsumption(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody EnergyConsumptionRequestDTO dto) {

        User user = authService.getAuthenticatedUser(request);

        EnergyConsumption updated = new EnergyConsumption();
        updated.setId(id);
        updated.setCategory(dto.getCategory());
        updated.setConsumptionKwh(dto.getConsumptionKwh());
        updated.setReferenceDate(dto.getReferenceDate());

        EnergyConsumption ec = energyConsumptionService.updateEnergyConsumption(updated, user);

        return ResponseEntity.ok(mapToDTO(ec));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnergyConsumption(
            HttpServletRequest request,
            @PathVariable Long id) {

        User user = authService.getAuthenticatedUser(request);
        energyConsumptionService.deleteEnergyConsumption(id, user);

        return ResponseEntity.noContent().build();
    }

    private EnergyConsumptionResponseDTO mapToDTO(EnergyConsumption ec) {
        EnergyConsumptionResponseDTO dto = new EnergyConsumptionResponseDTO();
        dto.setId(ec.getId());
        dto.setCategory(ec.getCategory());
        dto.setConsumptionKwh(ec.getConsumptionKwh());
        dto.setReferenceDate(ec.getReferenceDate());
        dto.setHouseholdId(ec.getHousehold().getId());
        dto.setCreatedAt(ec.getCreatedAt());
        dto.setUpdatedAt(ec.getUpdatedAt());
        return dto;
    }
}