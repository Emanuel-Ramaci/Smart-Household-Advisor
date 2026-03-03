package com.ramaci.energy_app.service;

import org.springframework.stereotype.Service;

import com.ramaci.energy_app.repository.HouseholdMemberRepository;
import com.ramaci.energy_app.repository.HouseholdRepository;
import com.ramaci.energy_app.repository.ExpenseRepository;
import com.ramaci.energy_app.repository.EnergyConsumptionRepository;
import com.ramaci.energy_app.dto.household.HouseholdRequestDTO;
import com.ramaci.energy_app.dto.household.HouseholdResponseDTO;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.model.HouseholdMember;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class HouseholdService {
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final EnergyConsumptionRepository energyConsumptionRepository;

    public HouseholdService(HouseholdRepository householdRepository,
            HouseholdMemberRepository householdMemberRepository,
            ExpenseRepository expenseRepository, EnergyConsumptionRepository energyConsumptionRepository) {
        this.householdRepository = householdRepository;
        this.householdMemberRepository = householdMemberRepository;
        this.expenseRepository = expenseRepository;
        this.energyConsumptionRepository = energyConsumptionRepository;
    }

    public List<HouseholdResponseDTO> getAllHouseholds() {
        return householdRepository.findAll().stream()
                .map(h -> {
                    HouseholdResponseDTO dto = new HouseholdResponseDTO();
                    dto.setId(h.getId());
                    dto.setName(h.getName());
                    dto.setAddress(h.getAddress() + ", " + h.getCity() + ", " + h.getPostalCode());
                    dto.setSquareMeters(h.getSquareMeters());
                    dto.setEnergyClass(h.getEnergyClass());
                    dto.setEnergyClass(h.getEnergyClass());
                    dto.setEnergyTariff(h.getEnergyTariff());
                    dto.setResidentsCount(h.getResidentsCount());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Household createHousehold(Household household, User user) {
        boolean exists = householdRepository.existsByNameAndAddressAndCityAndPostalCode(
                household.getName(),
                household.getAddress(),
                household.getCity(),
                household.getPostalCode());

        if (exists) {
            throw new RuntimeException("Casa già registrata con nome, indirizzo, città e codice postale: " +
                    household.getName() + ", " +
                    household.getAddress() + ", " +
                    household.getCity() + ", " +
                    household.getPostalCode());
        }

        household.setCreatedAt(LocalDateTime.now());
        Household savedHousehold = householdRepository.save(household);

        HouseholdMember member = new HouseholdMember();
        member.setHousehold(savedHousehold);
        member.setUser(user);
        member.setMembershipType("PROPRIETARIO"); // Supponiamo che chi sta creando la casa sia il proprietario di
                                                  // default

        householdMemberRepository.save(member);

        return savedHousehold;
    }

    public List<Household> getUserHouseholds(User user) {
        List<Household> households = householdMemberRepository
                .findByUserId(user.getId())
                .stream()
                .map(HouseholdMember::getHousehold)
                .toList();
        return households;
    }

    public Household updateHousehold(Long id, HouseholdRequestDTO input, User user) {

        Household existing = householdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + id));

        HouseholdMember member = householdMemberRepository
                .findByHouseholdAndUser(existing, user)
                .orElseThrow(() -> new RuntimeException("Non sei membro di questa casa"));

        if (!"PROPRIETARIO".equalsIgnoreCase(member.getMembershipType())) {
            throw new RuntimeException("Solo il PROPRIETARIO può modificare questa casa");
        }

        if (householdRepository.existsByNameAndIdNot(input.getName(), id)) {
            throw new RuntimeException("Nome della casa già esistente");
        }

        existing.setName(input.getName());
        existing.setAddress(input.getAddress());
        existing.setCity(input.getCity());
        existing.setPostalCode(input.getPostalCode());
        existing.setSquareMeters(input.getSquareMeters());
        existing.setEnergyClass(input.getEnergyClass());
        existing.setResidentsCount(input.getResidentsCount());
        existing.setEnergyTariff(input.getEnergyTariff());
        existing.setUpdatedAt(LocalDateTime.now());

        return householdRepository.save(existing);
    }

    public Household getHouseholdById(Long id, User user) {

        Household household = householdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + id));

        boolean isMember = householdMemberRepository
                .existsByHouseholdAndUser(household, user);

        if (!isMember) {
            throw new RuntimeException("Non hai i permessi per visualizzare questa casa");
        }

        return household;
    }

    @Transactional
    public void deleteHousehold(Long id, User user) {

        Household household = householdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + id));

        HouseholdMember member = householdMemberRepository
                .findByHouseholdAndUser(household, user)
                .orElseThrow(() -> new RuntimeException("Non sei membro di questa casa"));

        if (!"PROPRIETARIO".equalsIgnoreCase(member.getMembershipType())) {
            throw new RuntimeException("Solo il PROPRIETARIO può eliminare questa casa");
        }
        // Qui implementiamo un'eliminazione a cascata: laddove c'è una relazione con la
        // household che stiamo eliminando, l'altra parte verrà eliminata di
        // conseguenza
        householdMemberRepository.deleteByHousehold(household);
        expenseRepository.deleteByHousehold(household);
        energyConsumptionRepository.deleteByHousehold(household);

        householdRepository.delete(household);
    }
}
