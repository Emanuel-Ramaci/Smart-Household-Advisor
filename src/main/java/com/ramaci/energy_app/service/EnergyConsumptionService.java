package com.ramaci.energy_app.service;

import org.springframework.stereotype.Service;
import com.ramaci.energy_app.repository.EnergyConsumptionRepository;
import com.ramaci.energy_app.repository.HouseholdRepository;
import com.ramaci.energy_app.repository.HouseholdMemberRepository;
import com.ramaci.energy_app.dto.energy_consumption.EnergyConsumptionResponseDTO;
import com.ramaci.energy_app.model.EnergyConsumption;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
public class EnergyConsumptionService {

    private final EnergyConsumptionRepository energyConsumptionRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;

    public EnergyConsumptionService(EnergyConsumptionRepository energyConsumptionRepository,
            HouseholdRepository householdRepository,
            HouseholdMemberRepository householdMemberRepository) {
        this.energyConsumptionRepository = energyConsumptionRepository;
        this.householdRepository = householdRepository;
        this.householdMemberRepository = householdMemberRepository;
    }

    public List<EnergyConsumptionResponseDTO> getAllEnergyConsumptions() {
        return energyConsumptionRepository.findAll().stream()
                .map(ec -> {
                    EnergyConsumptionResponseDTO dto = new EnergyConsumptionResponseDTO();
                    dto.setId(ec.getId());
                    dto.setCategory(ec.getCategory());
                    dto.setConsumptionKwh(ec.getConsumptionKwh());
                    dto.setReferenceDate(ec.getReferenceDate());
                    dto.setHouseholdId(ec.getHousehold().getId());
                    dto.setCreatedAt(ec.getCreatedAt());
                    dto.setUpdatedAt(ec.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public EnergyConsumption createEnergyConsumption(EnergyConsumption ec, Long householdId, User user) {

        if (ec.getConsumptionKwh().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Il consumo in kWh deve essere positivo");
        }

        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + householdId));

        // Controllo che l'utente sia membro della household
        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(household, user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi aggiungere nuovi consumi");
        }

        boolean exists = energyConsumptionRepository.existsByHouseholdIdAndReferenceDateAndCategory(
                householdId, ec.getReferenceDate(), ec.getCategory());
        if (exists) {
            throw new RuntimeException(
                    "Consumo energetico già registrato per questa casa, data di riferimento e categoria");
        }

        EnergyConsumption createdEc = new EnergyConsumption();
        createdEc.setHousehold(household);
        createdEc.setCategory(ec.getCategory());
        createdEc.setConsumptionKwh(ec.getConsumptionKwh());
        createdEc.setReferenceDate(ec.getReferenceDate());
        createdEc.setCreatedAt(LocalDateTime.now());
        createdEc.setUpdatedAt(LocalDateTime.now());

        return energyConsumptionRepository.save(createdEc);
    }

    public EnergyConsumption getEnergyConsumptionById(Long id, User user) {
        EnergyConsumption ec = energyConsumptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo energetico non trovato con ID: " + id));

        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(ec.getHousehold(), user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi accedere a questo consumo");
        }
        return ec;
    }

    public List<EnergyConsumption> getAllByHousehold(Long householdId, User user) {
        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + householdId));

        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(household, user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi accedere ai consumi richiesti");
        }

        return energyConsumptionRepository.findByHouseholdId(householdId);
    }

    @Transactional
    public EnergyConsumption updateEnergyConsumption(EnergyConsumption updatedEc, User user) {
        EnergyConsumption existingEc = energyConsumptionRepository.findById(updatedEc.getId())
                .orElseThrow(() -> new RuntimeException("Consumo energetico non trovato con ID: " + updatedEc.getId()));

        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(existingEc.getHousehold(), user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi modificare questo consumo");
        }

        if (updatedEc.getConsumptionKwh().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Il consumo in kWh deve essere positivo");
        }

        existingEc.setCategory(updatedEc.getCategory());
        existingEc.setConsumptionKwh(updatedEc.getConsumptionKwh());
        existingEc.setReferenceDate(updatedEc.getReferenceDate());
        existingEc.setUpdatedAt(LocalDateTime.now());

        return energyConsumptionRepository.save(existingEc);
    }

    @Transactional
    public void deleteEnergyConsumption(Long id, User user) {
        EnergyConsumption ec = energyConsumptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo energetico non trovato con ID: " + id));

        boolean isMember = householdMemberRepository.existsByHouseholdAndUser(ec.getHousehold(), user);
        if (!isMember) {
            throw new RuntimeException("Non sei membro di questa casa, non puoi rimuovere questo consumo");
        }

        energyConsumptionRepository.delete(ec);
    }
}