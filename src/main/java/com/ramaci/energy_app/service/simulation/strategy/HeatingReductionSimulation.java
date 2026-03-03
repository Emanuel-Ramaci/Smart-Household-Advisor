package com.ramaci.energy_app.service.simulation.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.model.SimulationResult;

public class HeatingReductionSimulation implements SimulationStrategy {

        @Override
        public SimulationResult simulate(Household household, SimulationRequestDTO input) {

                BigDecimal current = input.getCurrentConsumptionKwh()
                                .setScale(2, RoundingMode.HALF_UP);

                int degrees = input.getReductionDegrees();

                // Solo una quota del consumo totale è legata al riscaldamento (ipotesi 50%)
                BigDecimal heatingShare = new BigDecimal("0.50");

                // Riduzione stimata del 4% per grado (valore più conservativo)
                BigDecimal reductionPerDegree = new BigDecimal("0.04");
                BigDecimal reductionFactor = reductionPerDegree
                                .multiply(BigDecimal.valueOf(degrees));

                // Limitiamo la riduzione massima al 25% per evitare scenari irrealistici
                BigDecimal maxReduction = new BigDecimal("0.25");
                if (reductionFactor.compareTo(maxReduction) > 0) {
                        reductionFactor = maxReduction;
                }

                // Separiamo quota riscaldamento e restante consumo
                BigDecimal heatingConsumption = current.multiply(heatingShare);
                BigDecimal nonHeatingConsumption = current.subtract(heatingConsumption);

                // Applichiamo la riduzione solo alla quota riscaldamento
                BigDecimal reducedHeating = heatingConsumption
                                .multiply(BigDecimal.ONE.subtract(reductionFactor));

                BigDecimal reducedTotal = reducedHeating
                                .add(nonHeatingConsumption)
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal savingKwh = current.subtract(reducedTotal)
                                .setScale(2, RoundingMode.HALF_UP);

                // Consideriamo che solo l'80% della tariffa sia realmente variabile
                BigDecimal variableTariffFactor = new BigDecimal("0.80");
                BigDecimal effectiveTariff = input.getEnergyTariffEuro()
                                .multiply(variableTariffFactor);

                BigDecimal savingEuro = savingKwh.multiply(effectiveTariff)
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal totalPrice = reducedTotal.multiply(effectiveTariff)
                                .setScale(2, RoundingMode.HALF_UP);

                SimulationResult result = new SimulationResult();
                result.setHousehold(household);
                result.setSimulationType("HEATING_REDUCTION");

                result.setInputData(
                                "{ \"currentConsumptionKwh\": " + current +
                                                ", \"reductionDegrees\": " + degrees +
                                                ", \"energyTariffEuro\": " + input.getEnergyTariffEuro() + " }");

                result.setResultData(
                                "{ \"estimatedReducedConsumptionKwh\": " + reducedTotal +
                                                ", \"estimatedTotalPriceEuro\": " + totalPrice + " }");

                result.setEstimatedSavingKwh(savingKwh);
                result.setEstimatedSavingEuro(savingEuro);

                return result;
        }
}