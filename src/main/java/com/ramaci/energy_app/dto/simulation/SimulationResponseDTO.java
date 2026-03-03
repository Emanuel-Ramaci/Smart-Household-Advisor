package com.ramaci.energy_app.dto.simulation;

import java.math.BigDecimal;

public class SimulationResponseDTO {
    private Long id;
    private String simulationType;
    private BigDecimal estimatedSavingEuro;
    private BigDecimal estimatedSavingKwh;
    private String resultData;

    public SimulationResponseDTO(Long id, String simulationType, BigDecimal estimatedSavingEuro,
            BigDecimal estimatedSavingKwh, String resultData) {
        this.id = id;
        this.simulationType = simulationType;
        this.estimatedSavingEuro = estimatedSavingEuro;
        this.estimatedSavingKwh = estimatedSavingKwh;
        this.resultData = resultData;
    }

    public String getSimulationType() {
        return simulationType;
    }

    public void setSimulationType(String simulationType) {
        this.simulationType = simulationType;
    }

    public BigDecimal getEstimatedSavingEuro() {
        return estimatedSavingEuro;
    }

    public void setEstimatedSavingEuro(BigDecimal estimatedSavingEuro) {
        this.estimatedSavingEuro = estimatedSavingEuro;
    }

    public BigDecimal getEstimatedSavingKwh() {
        return estimatedSavingKwh;
    }

    public void setEstimatedSavingKwh(BigDecimal estimatedSavingKwh) {
        this.estimatedSavingKwh = estimatedSavingKwh;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public SimulationResponseDTO() {
    }

    public SimulationResponseDTO(String simulationType, BigDecimal estimatedSavingEuro, BigDecimal estimatedSavingKwh,
            String resultData) {
        this.simulationType = simulationType;
        this.estimatedSavingEuro = estimatedSavingEuro;
        this.estimatedSavingKwh = estimatedSavingKwh;
        this.resultData = resultData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
