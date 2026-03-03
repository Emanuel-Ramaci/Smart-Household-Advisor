package com.ramaci.energy_app.service.simulation.proxy.cache;

import java.util.HashMap;
import java.util.Map;

import com.ramaci.energy_app.model.SimulationResult;
import com.ramaci.energy_app.service.simulation.ISimulationService;
import com.ramaci.energy_app.service.simulation.SimulationService;
import com.ramaci.energy_app.dto.simulation.SimulationRequestDTO;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class SimulationCacheProxy implements ISimulationService {

    private final ISimulationService simulationService;

    private static final int MAX_SIZE = 5; // Dopo il quinto risultato di simulazione salvato, il primo ad uscire sarà
                                           // il primo che è entrato (FIFO)
    // Time-To-Live
    private static final long TTL_MILLIS = 2 * 60 * 1000; // Dopo 2 minuti che il risultato è in cache verrà rimosso
                                                          // da
                                                          // essa

    private final Map<String, CacheEntry> cache = new HashMap<>();

    public SimulationCacheProxy(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    public SimulationResult runSimulation(HttpServletRequest request,
            String type,
            Long householdId,
            SimulationRequestDTO input) {

        // In questo caso la chiave rappresenta la concatenazione delle informazioni che
        // l'utente passa in input attraverso la richiesta. Se sono identiche a quelle
        // date in precedenza, la chiave salvata nella cache sarà identica di
        // conseguenza e verrà restituito il relativo risultato (se ancora presente in
        // cache) memorizzato.
        String key = type + "|" + householdId +
                "|" + input.getCurrentConsumptionKwh() +
                "|" + input.getReductionDegrees() +
                "|" + input.getStandbyConsumption() +
                "|" + input.getApplianceConsumption() +
                "|" + input.getEnergyTariffEuro();

        CacheEntry entry = cache.get(key);

        // Controllo se il risultato è effettivamente in cache e il TTL è valido
        if (entry != null && System.currentTimeMillis() - entry.timestamp < TTL_MILLIS) {
            System.out.println("[CacheProxy] Cache hit");
            return entry.result;
        }

        System.out.println("[CacheProxy] Cache miss");

        // Chiamo il servizio reale SOLO se il risultato non è in cache
        SimulationResult result = simulationService.runSimulation(request, type, householdId, input);

        // Prima di inserire il nuovo risultato nella cache controllo di non superare
        // la dimensione massima. Eventualemente eliminiamo dalla cache il risultato
        // meno recente.
        if (cache.size() >= MAX_SIZE) {
            String firstKey = cache.keySet().iterator().next();
            cache.remove(firstKey);
        }

        cache.put(key, new CacheEntry(result));

        return result;
    }

    private static class CacheEntry {
        SimulationResult result;
        long timestamp;

        CacheEntry(SimulationResult result) {
            this.result = result;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
