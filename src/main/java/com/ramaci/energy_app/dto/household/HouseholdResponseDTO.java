package com.ramaci.energy_app.dto.household;

import java.math.BigDecimal;

public class HouseholdResponseDTO {
    private Long id;
    private String name;
    private String address;
    private BigDecimal squareMeters;
    private String energyClass;
    private Integer residentsCount;
    private BigDecimal energyTariff;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
