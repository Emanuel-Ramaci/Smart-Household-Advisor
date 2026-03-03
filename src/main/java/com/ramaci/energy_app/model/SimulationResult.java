package com.ramaci.energy_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulation_results")
public class SimulationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @Column(name = "simulation_type", nullable = false, length = 50)
    private String simulationType;

    @Column(name = "input_data", nullable = false, columnDefinition = "JSON")
    private String inputData;

    @Column(name = "result_data", nullable = false, columnDefinition = "JSON")
    private String resultData;

    @Column(name = "estimated_saving_euro", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedSavingEuro;

    @Column(name = "estimated_saving_kwh", nullable = false, precision = 10, scale = 3)
    private BigDecimal estimatedSavingKwh;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SimulationResult() {
    }

    public SimulationResult(Long id, Household household, String simulationType, String inputData, String resultData,
            BigDecimal estimatedSavingEuro, BigDecimal estimatedSavingKwh, LocalDateTime createdAt) {
        this.id = id;
        this.household = household;
        this.simulationType = simulationType;
        this.inputData = inputData;
        this.resultData = resultData;
        this.estimatedSavingEuro = estimatedSavingEuro;
        this.estimatedSavingKwh = estimatedSavingKwh;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public String getSimulationType() {
        return simulationType;
    }

    public void setSimulationType(String simulationType) {
        this.simulationType = simulationType;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
