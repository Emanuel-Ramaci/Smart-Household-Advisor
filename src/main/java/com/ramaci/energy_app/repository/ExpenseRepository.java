package com.ramaci.energy_app.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ramaci.energy_app.model.Expense;
import com.ramaci.energy_app.model.Household;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<com.ramaci.energy_app.model.Expense, Long> {
    List<Expense> findByHousehold(Household household);

    List<Expense> findByHouseholdId(Long id);

    List<Expense> findByCategory(String category);

    List<Expense> findByAmount(Double amount);

    List<Expense> findByReferenceDate(LocalDate referenceDate);

    boolean existsByHouseholdIdAndReferenceDateAndCategory(Long householdId, LocalDate referenceDate, String category);

    void deleteByHousehold(Household household);
}
