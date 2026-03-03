package com.ramaci.energy_app.controller.view.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.ramaci.energy_app.dto.user.UserRequestDTO;

@Controller
public class UserViewController {
    @GetMapping("/user-login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserRequestDTO());
        return "user/register";
    }

    @GetMapping("/simulations")
    public String simulationsPage(@RequestParam Long householdId, Model model) {
        model.addAttribute("householdId", householdId);
        return "user/simulations";
    }

    @GetMapping("/energy-consumptions")
    public String energyConsumptionsPage(@RequestParam Long householdId, Model model) {
        model.addAttribute("householdId", householdId);
        return "user/consumptions";
    }

    @GetMapping("/expenses")
    public String expensesPage(@RequestParam Long householdId, Model model) {
        model.addAttribute("householdId", householdId);
        return "user/expenses";
    }

    @GetMapping("/household/new")
    public String householdCreationPage(@RequestParam Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "user/households";
    }

}
