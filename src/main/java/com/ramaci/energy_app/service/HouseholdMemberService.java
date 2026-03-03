package com.ramaci.energy_app.service;

import org.springframework.stereotype.Service;
import com.ramaci.energy_app.repository.HouseholdRepository;
import com.ramaci.energy_app.repository.UserRepository;
import com.ramaci.energy_app.repository.HouseholdMemberRepository;
import com.ramaci.energy_app.model.HouseholdMember;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseholdMemberService {
    private final HouseholdMemberRepository householdMemberRepository;
    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    public HouseholdMemberService(HouseholdMemberRepository householdMemberRepository,
            HouseholdRepository householdRepository, UserRepository userRepository) {
        this.householdMemberRepository = householdMemberRepository;
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
    }

    public List<HouseholdMember> getAllHouseholdMembers() {
        return householdMemberRepository.findAll().stream()
                .collect(Collectors.toList());
    }

    public HouseholdMember addHouseholdMember(Long householdId, Long userId, String membershipType) {
        if (householdMemberRepository.existsByHouseholdIdAndUserId(householdId, userId)) {
            throw new RuntimeException(
                    "L'utente con ID " + userId + " è già presente nella casa " + householdId);
        }
        HouseholdMember householdMember = new HouseholdMember();

        householdMember.setHousehold(
                householdRepository.findById(householdId)
                        .orElseThrow(() -> new RuntimeException("Casa non trovata con ID: " + householdId)));
        householdMember
                .setUser(userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + userId)));
        householdMember.setMembershipType(membershipType);

        return householdMemberRepository.save(householdMember);
    }

    public HouseholdMember getHouseholdMemberById(Long id) {
        return householdMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membro della casa non trovato con ID: " + id));
    }

    public List<HouseholdMember> getHouseholdMembers(Long id) {
        List<HouseholdMember> householdMembers = householdMemberRepository.findByHouseholdId(id);

        if (householdMembers.isEmpty()) {
            throw new RuntimeException("Nessun membro trovato della casa con ID: " + id);
        }

        return householdMembers;
    }

    public List<HouseholdMember> getHouseholdMembersByUserId(Long userId) {
        List<HouseholdMember> members = householdMemberRepository.findByUserId(userId);

        if (members.isEmpty()) {
            return List.of(); // meglio tornare lista vuota invece di eccezione
        }

        return members;
    }

    public HouseholdMember updateHouseholdMember(HouseholdMember householdMember) {
        HouseholdMember existingHouseholdMember = householdMemberRepository.findById(householdMember.getId())
                .orElseThrow(
                        () -> new RuntimeException("Membro della casa non trovato con ID: " + householdMember.getId()));

        existingHouseholdMember.setMembershipType(householdMember.getMembershipType());

        return householdMemberRepository.save(existingHouseholdMember);
    }

    public void deleteHouseholdMember(Long id) {
        if (!householdMemberRepository.existsByUserId(id)) {
            throw new RuntimeException("Membro della casa non trovato con ID: " + id);
        }
        householdMemberRepository.deleteById(id);
    }
}
