package com.ramaci.energy_app.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SimulationLoggingAspect {

    @Pointcut("execution(* com.ramaci.energy_app.service.simulation.ISimulationService.*(..))")
    public void runSimulationMethod() {
    }

    @Around("runSimulationMethod()")
    public Object logSimulation(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();
        String type = (String) args[1];
        Long householdId = (Long) args[2];

        System.out.println("[Aspect] Tentativo di simulazione - type: "
                + type + ", householdId: " + householdId);

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;

            System.out.println("[Aspect] SUCCESSO - completato in "
                    + duration + " ms");

            return result;

        } catch (RuntimeException ex) {

            long duration = System.currentTimeMillis() - start;

            System.out.println("[Aspect] ACCESSO NEGATO o ERRORE dopo "
                    + duration + " ms");
            System.out.println("[Aspect] Motivo: " + ex.getMessage());

            throw ex;
        }
    }
}