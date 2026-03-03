package com.ramaci.energy_app.controller.simulation;

import com.ramaci.energy_app.service.simulation.remote_facade.SimulationFacade;
import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;
import com.ramaci.energy_app.dto.simulation.SimulationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulations")
public class SimulationController {

    private final SimulationFacade simulationFacade;

    public SimulationController(SimulationFacade simulationFacade) {
        this.simulationFacade = simulationFacade;
    }

    @PostMapping("/heating")
    public SimulationResponseDTO heating(@RequestParam Long householdId,
            @RequestBody SimulationRequestDTO input,
            HttpServletRequest request) {

        return simulationFacade
                .runHeatingSimulation(request, householdId, input);
    }

    @PostMapping("/standby")
    public SimulationResponseDTO standBy(@RequestParam Long householdId,
            @RequestBody SimulationRequestDTO input,
            HttpServletRequest request) {

        return simulationFacade
                .runStandBySimulation(request, householdId, input);
    }

    @PostMapping("/timeshift")
    public SimulationResponseDTO timeShift(@RequestParam Long householdId,
            @RequestBody SimulationRequestDTO input,
            HttpServletRequest request) {

        return simulationFacade
                .runTimeShiftSimulation(request, householdId, input);
    }
}