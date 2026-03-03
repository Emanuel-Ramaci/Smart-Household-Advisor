package com.ramaci.energy_app.aspect;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import com.ramaci.energy_app.model.User;
import com.ramaci.energy_app.service.AuthService;

@Aspect
@Component
public class SimulationResultLoggingAspect {

    private final AuthService authService;

    public SimulationResultLoggingAspect(AuthService authService) {
        this.authService = authService;
    }

    // Cattura tutti i metodi di ISimulationResultService
    @Pointcut("execution(* com.ramaci.energy_app.service.simulation.ISimulationResultService.*(..))")
    public void simulationResultMethods() {
    }

    @Around("simulationResultMethods()")
    public Object logSimulationResult(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = null;
        Long householdId = null;
        String type = null;

        for (Object arg : args) {
            if (arg instanceof HttpServletRequest)
                request = (HttpServletRequest) arg;
            else if (arg instanceof Long)
                householdId = (Long) arg;
            else if (arg instanceof String)
                type = (String) arg;
        }

        String userIdStr = "UNKNOWN";
        if (request != null) {
            try {
                User loggedUser = authService.getAuthenticatedUser(request);
                userIdStr = loggedUser != null ? loggedUser.getId().toString() : "UNKNOWN";
            } catch (Exception ex) {
                // Anche se l'utente non è autorizzato, il logging non viene interrotto
            }
        }

        System.out.println("[Aspect] Utente " + userIdStr +
                " tenta di accedere ai risultati simulazione" +
                (householdId != null ? " - householdId: " + householdId : "") +
                (type != null ? ", type: " + type : ""));

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;
            System.out.println("[Aspect] SUCCESSO - metodo completato in " + duration + " ms");

            return result;

        } catch (RuntimeException ex) {

            long duration = System.currentTimeMillis() - start;
            System.out.println("[Aspect] ACCESSO NEGATO o ERRORE dopo " + duration + " ms");
            System.out.println("[Aspect] Utente " + userIdStr +
                    " non autorizzato a operare su householdId: " + householdId);
            System.out.println("[Aspect] Motivo: " + ex.getMessage());

            throw ex;
        }
    }
}