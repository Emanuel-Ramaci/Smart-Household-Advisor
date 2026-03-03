package com.ramaci.energy_app.controller.view.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.ramaci.energy_app.service.HouseholdMemberService;
import com.ramaci.energy_app.service.AuthService;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.model.HouseholdMember;
import com.ramaci.energy_app.model.Household;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeViewController {

    private final HouseholdMemberService householdMemberService;
    private final AuthService authService;

    public HomeViewController(HouseholdMemberService householdMemberService,
            AuthService authService) {
        this.householdMemberService = householdMemberService;
        this.authService = authService;
    }

    @GetMapping
    public String homePage(HttpServletRequest request, Model model) {

        User user = authService.getAuthenticatedUser(request);

        // Prendo tutti i membri della household dell'utente
        List<HouseholdMember> memberships = householdMemberService.getHouseholdMembersByUserId(user.getId());

        // Estraggo le household
        List<Household> households = memberships.stream()
                .map(HouseholdMember::getHousehold)
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("households", households);

        return "user/home";
    }
}