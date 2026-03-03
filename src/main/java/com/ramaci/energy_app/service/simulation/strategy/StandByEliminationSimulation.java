package com.ramaci.energy_app.service.simulation.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.model.SimulationResult;

public class StandByEliminationSimulation implements SimulationStrategy {

        @Override
        public SimulationResult simulate(Household household, SimulationRequestDTO input) {

                BigDecimal standbyConsumption = input.getStandbyConsumption()
                                .setScale(2, RoundingMode.HALF_UP);

                // Non tutto lo standby è eliminabile (ipotesi 90%)
                BigDecimal eliminationFactor = new BigDecimal("0.90");

                BigDecimal effectiveEliminated = standbyConsumption
                                .multiply(eliminationFactor)
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal savingKwh = effectiveEliminated;

                // Solo parte variabile della tariffa
                BigDecimal variableTariffFactor = new BigDecimal("0.80");
                BigDecimal effectiveTariff = input.getEnergyTariffEuro()
                                .multiply(variableTariffFactor);

                BigDecimal savingEuro = savingKwh.multiply(effectiveTariff)
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal totalPrice = BigDecimal.ZERO;

                SimulationResult result = new SimulationResult();
                result.setHousehold(household);
                result.setSimulationType("STANDBY_ELIMINATION");

                result.setInputData(
                                "{ \"standbyConsumption\": " + standbyConsumption +
                                                ", \"energyTariffEuro\": " + input.getEnergyTariffEuro() + " }");

                result.setResultData(
                                "{ \"estimatedEliminatedConsumptionKwh\": " + effectiveEliminated +
                                                ", \"estimatedTotalPriceEuro\": " + totalPrice + " }");

                result.setEstimatedSavingKwh(savingKwh);
                result.setEstimatedSavingEuro(savingEuro);

                return result;
        }
}