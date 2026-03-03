package com.ramaci.energy_app.controller;

import com.ramaci.energy_app.dto.expense.ExpenseRequestDTO;
import com.ramaci.energy_app.dto.expense.ExpenseResponseDTO;
import com.ramaci.energy_app.model.Expense;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.service.AuthService;
import com.ramaci.energy_app.service.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final AuthService authService;

    public ExpenseController(ExpenseService expenseService,
            AuthService authService) {
        this.expenseService = expenseService;
        this.authService = authService;
    }

    // Creazione di una spesa per una specifica casa
    @PostMapping("/household/{householdId}")
    public ResponseEntity<ExpenseResponseDTO> createExpense(
            HttpServletRequest request,
            @PathVariable Long householdId,
            @RequestBody ExpenseRequestDTO dto) {

        User user = authService.getAuthenticatedUser(request);
        Expense expense = new Expense();
        expense.setCategory(dto.getCategory());
        expense.setAmount(dto.getAmount());
        expense.setReferenceDate(dto.getReferenceDate());
        expense.setDescription(dto.getDescription());

        Expense createdExpense = expenseService.createExpense(expense,
                householdId, user);

        ExpenseResponseDTO response = mapToDTO(createdExpense);

        return ResponseEntity.ok(response);
    }

    // Accesso ad una specifica spesa
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpense(
            HttpServletRequest request,
            @PathVariable Long id) {

        User user = authService.getAuthenticatedUser(request);
        Expense expense = expenseService.getExpenseById(id, user);

        return ResponseEntity.ok(mapToDTO(expense));
    }

    // Accesso a tutte le spese relative ad una specifica casa
    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getAllByHousehold(
            HttpServletRequest request,
            @PathVariable Long householdId) {

        User user = authService.getAuthenticatedUser(request);
        List<Expense> list = expenseService.getAllByHousehold(householdId, user);

        List<ExpenseResponseDTO> responseList = list.stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody ExpenseRequestDTO dto) {

        User user = authService.getAuthenticatedUser(request);

        Expense updated = new Expense();
        updated.setId(id);
        updated.setCategory(dto.getCategory());
        updated.setAmount(dto.getAmount());
        updated.setReferenceDate(dto.getReferenceDate());
        updated.setDescription(dto.getDescription());

        Expense expense = expenseService.updateExpense(updated, user);

        return ResponseEntity.ok(mapToDTO(expense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            HttpServletRequest request,
            @PathVariable Long id) {

        User user = authService.getAuthenticatedUser(request);
        expenseService.deleteExpense(id, user);

        return ResponseEntity.noContent().build();
    }

    private ExpenseResponseDTO mapToDTO(Expense expense) {
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(expense.getId());
        dto.setCategory(expense.getCategory());
        dto.setAmount(expense.getAmount());
        dto.setReferenceDate(expense.getReferenceDate());
        dto.setDescription(expense.getDescription());
        dto.setHouseholdId(expense.getHousehold().getId());
        dto.setCreatedAt(expense.getCreatedAt());
        dto.setUpdatedAt(expense.getUpdatedAt());
        return dto;
    }
}
