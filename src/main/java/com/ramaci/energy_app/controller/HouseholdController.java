package com.ramaci.energy_app.controller;

import com.ramaci.energy_app.dto.household.HouseholdRequestDTO;
import com.ramaci.energy_app.dto.household.HouseholdResponseDTO;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.service.AuthService;
import com.ramaci.energy_app.service.HouseholdService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/household")
public class HouseholdController {

    private final HouseholdService householdService;
    private final AuthService authService;

    public HouseholdController(HouseholdService householdService,
            AuthService authService) {
        this.householdService = householdService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<HouseholdResponseDTO> createHousehold(
            HttpServletRequest request,
            @RequestBody HouseholdRequestDTO input) {

        User user = authService.getAuthenticatedUser(request);
        System.out.println("POST user ID: " + user.getId());
        Household household = new Household();
        household.setName(input.getName());
        household.setAddress(input.getAddress());
        household.setCity(input.getCity());
        household.setPostalCode(input.getPostalCode());
        household.setSquareMeters(input.getSquareMeters());
        household.setEnergyClass(input.getEnergyClass());
        household.setResidentsCount(input.getResidentsCount());
        household.setEnergyTariff(input.getEnergyTariff());

        Household createdHousehold = householdService.createHousehold(household, user);

        HouseholdResponseDTO response = mapToDTO(createdHousehold);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<HouseholdResponseDTO>> getMyHouseholds(HttpServletRequest request) {
        User user = authService.getAuthenticatedUser(request);
        System.out.println("GET user ID: " + user.getId());
        // Trova tutte le household a cui l'utente appartiene
        List<Household> households = householdService.getUserHouseholds(user);

        List<HouseholdResponseDTO> response = households.stream()
                .map(this::mapToDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HouseholdResponseDTO> updateHousehold(
            @PathVariable Long id,
            HttpServletRequest request,
            @RequestBody HouseholdRequestDTO input) {

        User user = authService.getAuthenticatedUser(request);

        Household updatedHousehold = householdService.updateHousehold(id, input, user);

        HouseholdResponseDTO response = mapToDTO(updatedHousehold);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HouseholdResponseDTO> getHousehold(
            @PathVariable Long id,
            HttpServletRequest request) {

        User user = authService.getAuthenticatedUser(request);

        Household household = householdService.getHouseholdById(id, user);

        HouseholdResponseDTO response = mapToDTO(household);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHousehold(
            @PathVariable Long id,
            HttpServletRequest request) {

        User user = authService.getAuthenticatedUser(request);

        householdService.deleteHousehold(id, user);

        return ResponseEntity.noContent().build();
    }

    private HouseholdResponseDTO mapToDTO(Household household) {
        HouseholdResponseDTO response = new HouseholdResponseDTO();
        response.setId(household.getId());
        response.setName(household.getName());
        response.setAddress(household.getAddress() + ", " + household.getCity() + ", " + household.getPostalCode());
        response.setSquareMeters(household.getSquareMeters());
        response.setEnergyClass(household.getEnergyClass());
        response.setResidentsCount(household.getResidentsCount());
        response.setEnergyTariff(household.getEnergyTariff());
        return response;
    }
}
