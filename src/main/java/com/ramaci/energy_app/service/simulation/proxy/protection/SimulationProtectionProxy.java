package com.ramaci.energy_app.service.simulation.proxy.protection;

import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;
import com.ramaci.energy_app.service.AuthService;
import com.ramaci.energy_app.service.simulation.ISimulationService;
import com.ramaci.energy_app.repository.HouseholdRepository;
import com.ramaci.energy_app.service.simulation.proxy.cache.SimulationCacheProxy;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

@Service
@Primary // Mettiamo Primary perché anche SimulationService implementa la stessa
         // interfaccia e
         // Spring potrebbe andare direttamente al SimulationService (servizio reale)
         // bypassando il Proxy
public class SimulationProtectionProxy implements ISimulationService {

    private final ISimulationService simulationService; // Questo sarà il riferimento al Cache Proxy
    private final AuthService authService;
    private final HouseholdRepository householdRepository;

    public SimulationProtectionProxy(SimulationCacheProxy simulationService, AuthService authService,
            HouseholdRepository householdRepository) {
        this.simulationService = simulationService;
        this.authService = authService;
        this.householdRepository = householdRepository;
    }

    @Override
    public SimulationResult runSimulation(HttpServletRequest request, String type, Long householdId,
            SimulationRequestDTO input) {
        User loggedUser = authService.getAuthenticatedUser(request);

        String permission = switch (type) {
            case "HEATING" -> "USER_RUN_HEATING_SIMULATION";
            case "STANDBY" -> "USER_RUN_STANDBY_SIMULATION";
            case "TIMESHIFT" -> "USER_RUN_TIMESHIFT_SIMULATION";
            default -> throw new IllegalArgumentException("Tipo simulazione non valido");
        };
        authService.checkPermission(loggedUser, permission);

        // Controllo membership della household
        householdRepository
                .findAccessibleHousehold(householdId, loggedUser.getId())
                .orElseThrow(() -> new RuntimeException("Accesso negato: household non accessibile"));

        // Passa la chiamata al servizio reale
        return simulationService.runSimulation(request, type, householdId, input);
    }
}
