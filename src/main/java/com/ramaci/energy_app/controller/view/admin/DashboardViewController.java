package com.ramaci.energy_app.controller.view.admin;

import com.ramaci.energy_app.service.user.AdminService;
import com.ramaci.energy_app.service.HouseholdService;
import com.ramaci.energy_app.service.HouseholdMemberService;
import com.ramaci.energy_app.service.simulation.SimulationResultService;
import com.ramaci.energy_app.service.EnergyConsumptionService;
import com.ramaci.energy_app.service.ExpenseService;
import com.ramaci.energy_app.service.AuthService;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import com.ramaci.energy_app.model.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class DashboardViewController {

    private final AdminService adminService;
    private final HouseholdService householdService;
    private final HouseholdMemberService householdMemberService;
    private final SimulationResultService simulationResultService;
    private final ExpenseService expenseService;
    private final EnergyConsumptionService energyConsumptionService;
    private final AuthService authService;

    public DashboardViewController(
            AdminService adminService,
            HouseholdService householdService,
            HouseholdMemberService householdMemberService,
            SimulationResultService simulationResultService,
            ExpenseService expenseService,
            EnergyConsumptionService energyConsumptionService,
            AuthService authService) {
        this.adminService = adminService;
        this.householdService = householdService;
        this.householdMemberService = householdMemberService;
        this.simulationResultService = simulationResultService;
        this.expenseService = expenseService;
        this.energyConsumptionService = energyConsumptionService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        User admin = authService.getAuthenticatedUser(request);
        if (!authService.isAdmin(admin)) {
            throw new RuntimeException("Accesso negato: ruolo ADMIN richiesto");
        }
        model.addAttribute("user", admin);

        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("households", householdService.getAllHouseholds());
        model.addAttribute("householdMembers", householdMemberService.getAllHouseholdMembers());
        model.addAttribute("simulationResults", simulationResultService.getAllSimulationResults());
        model.addAttribute("expenses", expenseService.getAllExpenses());
        model.addAttribute("energyConsumptions", energyConsumptionService.getAllEnergyConsumptions());

        return "admin/dashboard";
    }
}