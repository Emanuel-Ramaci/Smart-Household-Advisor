package com.ramaci.energy_app.service;

import org.springframework.stereotype.Service;

import com.ramaci.energy_app.repository.ExpenseRepository;
import com.ramaci.energy_app.repository.HouseholdRepository;
import com.ramaci.energy_app.repository.HouseholdMemberRepository;
import com.ramaci.energy_app.dto.expense.ExpenseResponseDTO;
import com.ramaci.energy_app.model.Expense;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
            HouseholdRepository householdRepository,
            HouseholdMemberRepository householdMemberRepository) {
        this.expenseRepository = expenseRepository;
        this.householdRepository = householdRepository;
        this.householdMemberRepository = householdMemberRepository;
    }

    public List<ExpenseResponseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(ex -> {
                    ExpenseResponseDTO dto = new ExpenseResponseDTO();
                    dto.setId(ex.getId());
                    dto.setCategory(ex.getCategory());
                    dto.setAmount(ex.getAmount());
                    dto.setReferenceDate(ex.getReferenceDate());
                    dto.setDescription(ex.getDescription());
                    dto.setHouseholdId(ex.getHousehold().getId());
                    dto.setCreatedAt(ex.getCreatedAt());
                    dto.setUpdatedAt(ex.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Expense createExpense(Expense expense, Long householdId, User user) {

        if (expense.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La quantità di denaro spesa in euro deve essere positiva");
        }

        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + householdId));

        // Controllo che l'utente sia membro della household
        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(household, user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi aggiungere nuove spese");
        }

        boolean exists = expenseRepository.existsByHouseholdIdAndReferenceDateAndCategory(
                householdId, expense.getReferenceDate(), expense.getCategory());
        if (exists) {
            throw new RuntimeException(
                    "Spesa già registrata per questa casa, data di riferimento, categoria e descrizione");
        }

        Expense createdExpense = new Expense();
        createdExpense.setHousehold(household);
        createdExpense.setCategory(expense.getCategory());
        createdExpense.setAmount(expense.getAmount());
        createdExpense.setReferenceDate(expense.getReferenceDate());
        createdExpense.setDescription(expense.getDescription());
        createdExpense.setCreatedAt(LocalDateTime.now());
        createdExpense.setUpdatedAt(LocalDateTime.now());

        return expenseRepository.save(createdExpense);
    }

    public Expense getExpenseById(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spesa non trovata con ID: " + id));

        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(expense.getHousehold(), user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi accedere a questa spesa");
        }
        return expense;
    }

    public List<Expense> getAllByHousehold(Long householdId, User user) {
        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + householdId));

        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(household, user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi accedere alle spese richieste");
        }

        return expenseRepository.findByHouseholdId(householdId);
    }

    @Transactional
    public Expense updateExpense(Expense updatedExpense, User user) {
        Expense existingExpense = expenseRepository.findById(updatedExpense.getId())
                .orElseThrow(
                        () -> new RuntimeException("Spesa non trovata con ID: " + updatedExpense.getId()));

        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(existingExpense.getHousehold(), user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi modificare questa spesa");
        }

        if (updatedExpense.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La quantità di denaro in euro deve essere positiva");
        }

        existingExpense.setCategory(updatedExpense.getCategory());
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setReferenceDate(updatedExpense.getReferenceDate());
        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setUpdatedAt(LocalDateTime.now());

        return expenseRepository.save(existingExpense);
    }

    @Transactional
    public void deleteExpense(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spesa non trovata con ID: " + id));

        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(expense.getHousehold(), user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi rimuovere questa spesa");
        }

        expenseRepository.delete(expense);
    }
}