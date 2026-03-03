package com.ramaci.energy_app.service.simulation.proxy.cache;

import com.ramaci.energy_app.service.simulation.ISimulationResultService;
import com.ramaci.energy_app.service.simulation.SimulationResultService;
import com.ramaci.energy_app.model.SimulationResult;

import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class SimulationResultCacheProxy implements ISimulationResultService {

    private final ISimulationResultService realService;

    private static final int MAX_SIZE = 5;
    private static final long TTL_MILLIS = 1 / 2 * 60 * 1000;

    private final Map<String, CacheEntry> cache = new HashMap<>();

    public SimulationResultCacheProxy(SimulationResultService realService) {
        this.realService = realService;
    }

    @Override
    public List<SimulationResult> getSimulationResultsByHouseholdId(HttpServletRequest request, Long householdId) {
        String key = "household|" + householdId;

        CacheEntry entry = cache.get(key);
        if (entry != null && System.currentTimeMillis() - entry.timestamp < TTL_MILLIS) {
            System.out.println("[CacheProxy] Cache hit per household " + householdId);
            return entry.results;
        }

        System.out.println("[CacheProxy] Cache miss per household " + householdId);
        List<SimulationResult> results = realService.getSimulationResultsByHouseholdId(request, householdId);

        addToCache(key, results);
        return results;
    }

    @Override
    public List<SimulationResult> getSimulationResultsByHouseholdIdAndSimulationType(
            HttpServletRequest request, Long householdId, String simulationType) {

        String key = "household|" + householdId + "|type|" + simulationType;

        CacheEntry entry = cache.get(key);
        if (entry != null && System.currentTimeMillis() - entry.timestamp < TTL_MILLIS) {
            System.out.println("[CacheProxy] Cache hit per household " + householdId + " type " + simulationType);
            return entry.results;
        }

        System.out.println("[CacheProxy] Cache miss per household " + householdId + " type " + simulationType);
        List<SimulationResult> results = realService.getSimulationResultsByHouseholdIdAndSimulationType(
                request, householdId, simulationType);

        addToCache(key, results);
        return results;
    }

    @Override
    public void deleteSimulationResultById(HttpServletRequest request, Long householdId, Long id) {
        System.out.println(
                "[CacheProxy] Rimozione dalla cache del risultato  di simulazione (con id " + id + ") per household "
                        + householdId);
        // Rimuove tutte le cache legate a questa household
        cache.keySet().removeIf(k -> k.startsWith("household|" + householdId));

        realService.deleteSimulationResultById(request, householdId, id);
    }

    private void addToCache(String key, List<SimulationResult> results) {
        if (cache.size() >= MAX_SIZE) {
            String firstKey = cache.keySet().iterator().next();
            cache.remove(firstKey);
        }
        cache.put(key, new CacheEntry(results));
    }

    private static class CacheEntry {
        List<SimulationResult> results;
        long timestamp;

        CacheEntry(List<SimulationResult> results) {
            this.results = results;
            this.timestamp = System.currentTimeMillis();
        }
    }
}