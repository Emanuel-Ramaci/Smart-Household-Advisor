package com.ramaci.energy_app.service.simulation.proxy.protection;

import org.springframework.stereotype.Service;

import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.repository.HouseholdRepository;
import com.ramaci.energy_app.service.AuthService;
import com.ramaci.energy_app.service.simulation.ISimulationResultService;
import com.ramaci.energy_app.service.simulation.proxy.cache.SimulationResultCacheProxy;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import org.springframework.context.annotation.Primary;

@Service
@Primary
public class SimulationResultProtectionProxy implements ISimulationResultService {

    private final ISimulationResultService simulationResultService; // Questo sarà il riferimento al Cache Proxy
    private final AuthService authService;
    private final HouseholdRepository householdRepository;

    public SimulationResultProtectionProxy(SimulationResultCacheProxy simulationResultService,
            AuthService authService,
            HouseholdRepository householdRepository) {
        this.simulationResultService = simulationResultService;
        this.authService = authService;
        this.householdRepository = householdRepository;
    }

    private void checkAccess(HttpServletRequest request, Long householdId) {
        User loggedUser = authService.getAuthenticatedUser(request);
        authService.checkPermission(loggedUser, "USER_READ_SIMULATION_RESULTS");

        householdRepository.findAccessibleHousehold(householdId, loggedUser.getId())
                .orElseThrow(() -> new RuntimeException("Accesso negato: household non accessibile"));
    }

    public List<SimulationResult> getSimulationResultsByHouseholdId(HttpServletRequest request, Long householdId) {
        checkAccess(request, householdId);
        return simulationResultService.getSimulationResultsByHouseholdId(request, householdId);
    }

    public List<SimulationResult> getSimulationResultsByHouseholdIdAndSimulationType(HttpServletRequest request,
            Long householdId, String type) {
        checkAccess(request, householdId);
        return simulationResultService.getSimulationResultsByHouseholdIdAndSimulationType(request, householdId, type);
    }

    public void deleteSimulationResultById(HttpServletRequest request, Long householdId, Long id) {
        checkAccess(request, householdId);
        simulationResultService.deleteSimulationResultById(request, householdId, id);
    }

}