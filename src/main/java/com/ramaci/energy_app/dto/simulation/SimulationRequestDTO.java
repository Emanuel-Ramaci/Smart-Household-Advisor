package com.ramaci.energy_app.dto.simulation;

import java.math.BigDecimal;

public class SimulationRequestDTO {
    private BigDecimal currentConsumptionKwh;
    private BigDecimal energyTariffEuro;
    private int reductionDegrees; // Per il riscaldamento
    private BigDecimal standbyConsumption;
    private BigDecimal applianceConsumption;

    public BigDecimal getCurrentConsumptionKwh() {
        return currentConsumptionKwh;
    }

    public void setCurrentConsumptionKwh(BigDecimal currentConsumptionKwh) {
        this.currentConsumptionKwh = currentConsumptionKwh;
    }

    public BigDecimal getEnergyTariffEuro() {
        return energyTariffEuro;
    }

    public void setEnergyTariffEuro(BigDecimal energyTariffEuro) {
        this.energyTariffEuro = energyTariffEuro;
    }

    public int getReductionDegrees() {
        return reductionDegrees;
    }

    public void setReductionDegrees(int reductionDegrees) {
        this.reductionDegrees = reductionDegrees;
    }

    public BigDecimal getStandbyConsumption() {
        return standbyConsumption;
    }

    public void setStandbyConsumption(BigDecimal standbyConsumption) {
        this.standbyConsumption = standbyConsumption;
    }

    public BigDecimal getApplianceConsumption() {
        return applianceConsumption;
    }

    public void setApplianceConsumption(BigDecimal applianceConsumption) {
        this.applianceConsumption = applianceConsumption;
    }

}
