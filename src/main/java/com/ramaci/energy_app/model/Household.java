package com.ramaci.energy_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "households")
public class Household {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String address = "null";

    @Column
    private String city = "null";

    @Column(length = 5)
    private Integer postalCode = 0;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal squareMeters;

    @Column(nullable = false, length = 5)
    private String energyClass;

    @Column(nullable = false)
    private Integer residentsCount;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal energyTariff;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HouseholdMember> members = new HashSet<>();

    public Household() {
    }

    public Household(Long id, String name, String address, String city, Integer postalCode, BigDecimal squareMeters,
            String energyClass, Integer residentsCount, BigDecimal energyTariff, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.squareMeters = squareMeters;
        this.energyClass = energyClass;
        this.residentsCount = residentsCount;
        this.energyTariff = energyTariff;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getSquareMeters() {
        return squareMeters;
    }

    public void setSquareMeters(BigDecimal squareMeters) {
        this.squareMeters = squareMeters;
    }

    public String getEnergyClass() {
        return energyClass;
    }

    public void setEnergyClass(String energyClass) {
        this.energyClass = energyClass;
    }

    public Integer getResidentsCount() {
        return residentsCount;
    }

    public void setResidentsCount(Integer residentsCount) {
        this.residentsCount = residentsCount;
    }

    public BigDecimal getEnergyTariff() {
        return energyTariff;
    }

    public void setEnergyTariff(BigDecimal energyTariff) {
        this.energyTariff = energyTariff;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<HouseholdMember> getMembers() {
        return members;
    }

    public void setMembers(Set<HouseholdMember> members) {
        this.members = members;
    }
}
