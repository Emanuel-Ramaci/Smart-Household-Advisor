package com.ramaci.energy_app.service.simulation.strategy;

import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.model.Household;
import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TimeShiftSimulation implements SimulationStrategy {

        @Override
        public SimulationResult simulate(Household household, SimulationRequestDTO input) {

                BigDecimal applianceConsumption = input.getApplianceConsumption()
                                .setScale(2, RoundingMode.HALF_UP);

                // Il consumo non cambia: spostare fascia non riduce i kWh
                BigDecimal savingKwh = BigDecimal.ZERO;

                // Ipotesi: tariffa off-peak del 20% più bassa
                BigDecimal offPeakFactor = new BigDecimal("0.80");

                BigDecimal originalTariff = input.getEnergyTariffEuro();

                // Consideriamo solo la parte variabile della tariffa (80%)
                BigDecimal variableTariffFactor = new BigDecimal("0.80");
                BigDecimal effectiveOriginalTariff = originalTariff.multiply(variableTariffFactor);

                BigDecimal effectiveOffPeakTariff = effectiveOriginalTariff
                                .multiply(offPeakFactor);

                BigDecimal originalCost = applianceConsumption
                                .multiply(effectiveOriginalTariff);

                BigDecimal newCost = applianceConsumption
                                .multiply(effectiveOffPeakTariff)
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal savingEuro = originalCost.subtract(newCost)
                                .setScale(2, RoundingMode.HALF_UP);

                SimulationResult result = new SimulationResult();
                result.setHousehold(household);
                result.setSimulationType("TIME_SHIFT");

                result.setInputData(
                                "{ \"applianceConsumption\": " + applianceConsumption +
                                                ", \"energyTariffEuro\": " + originalTariff + " }");

                result.setResultData(
                                "{ \"estimatedReducedConsumptionKwh\": " + applianceConsumption +
                                                ", \"estimatedTotalPriceEuro\": " + newCost + " }");

                result.setEstimatedSavingKwh(savingKwh);
                result.setEstimatedSavingEuro(savingEuro);

                return result;
        }
}