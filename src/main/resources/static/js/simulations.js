document.addEventListener("DOMContentLoaded", () => {
  const householdElement = document.getElementById("householdId");
  if (!householdElement) return;

  const householdId = householdElement.innerText.trim();
  const tbody = document.querySelector("#simulationHistory tbody");
  if (!tbody) return;

  /* DELETE */

  tbody.addEventListener("click", async (e) => {
    if (e.target.classList.contains("delete-btn")) {
      const resultId = e.target.dataset.id;
      if (!resultId) return;

      if (confirm("Sei sicuro di voler eliminare questa simulazione?")) {
        try {
          await fetch(`/api/simulation-results/${householdId}/${resultId}`, {
            method: "DELETE",
          });
          refreshSimulationHistory();
        } catch (err) {
          console.error("Errore eliminando simulazione:", err);
        }
      }
    }
  });

  /* REFRESH HISTORY */

  async function refreshSimulationHistory() {
    try {
      const res = await fetch(`/api/simulation-results/${householdId}`);
      const simulations = await res.json();
      tbody.innerHTML = "";

      simulations.forEach((sim, index) => {
        let details = "";

        try {
          const parsed = JSON.parse(sim.resultData);

          const labels = {
            estimatedTotalPriceEuro: "Prezzo totale stimato",
            estimatedReducedConsumptionKwh: "Consumo ridotto stimato",
            estimatedEliminatedConsumptionKwh: "Consumo eliminato stimato",
          };

          const units = {
            estimatedTotalPriceEuro: "€",
            estimatedReducedConsumptionKwh: "kWh",
            estimatedEliminatedConsumptionKwh: "kWh",
          };

          details = Object.entries(parsed)
            .map(([k, v]) => {
              if (units[k]) {
                v = Number(v).toFixed(2).replace(".", ",");
              }
              return `${labels[k] || k}: ${v}${units[k] ? " " + units[k] : ""}`;
            })
            .join("<br>");
        } catch {
          details = sim.resultData;
        }

        const formattedKwh = Number(sim.estimatedSavingKwh)
          .toFixed(2)
          .replace(".", ",");

        const formattedEuro = Number(sim.estimatedSavingEuro)
          .toFixed(2)
          .replace(".", ",");

        const typeLabel =
          sim.simulationType === "HEATING_REDUCTION"
            ? "Heating Reduction"
            : sim.simulationType === "TIME_SHIFT"
              ? "Time Shift"
              : sim.simulationType === "STANDBY_ELIMINATION"
                ? "Standby Elimination"
                : sim.simulationType;

        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${index + 1}</td>
          <td>${typeLabel}</td>
          <td>${formattedKwh} kWh</td>
          <td>${formattedEuro} €</td>
          <td>${details}</td>
          <td><button class="delete-btn" data-id="${sim.id}">Elimina</button></td>
        `;

        tbody.appendChild(row);
      });
    } catch (err) {
      console.error("Errore caricando simulazioni:", err);
    }
  }

  refreshSimulationHistory();

  /* RUN SIMULATION */

  async function runSimulation(endpoint, payload, resultElementId) {
    try {
      const res = await fetch(
        `/api/simulations/${endpoint}?householdId=${householdId}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        },
      );

      if (!res.ok) throw new Error("Errore nel salvataggio");

      const data = await res.json();

      const formattedSavingKwh = Number(data.estimatedSavingKwh)
        .toFixed(2)
        .replace(".", ",");

      const formattedEuro = Number(data.estimatedSavingEuro)
        .toFixed(2)
        .replace(".", ",");

      document.getElementById(resultElementId).innerText =
        `Risultato ➤ Risparmio in kWh = ${formattedSavingKwh} kWh - Risparmio in euro = ${formattedEuro} €`;

      setTimeout(refreshSimulationHistory, 200);
    } catch (err) {
      document.getElementById(resultElementId).innerText = `${err}`;
    }
  }

  /* FORMS */

  document.getElementById("heatingForm")?.addEventListener("submit", (e) => {
    e.preventDefault();
    runSimulation(
      "heating",
      {
        currentConsumptionKwh: parseFloat(
          document.getElementById("currentConsumptionKwh").value,
        ),
        reductionDegrees: parseInt(
          document.getElementById("reductionDegrees").value,
        ),
        energyTariffEuro: parseFloat(
          document.getElementById("energyTariffHeating").value,
        ),
      },
      "heatingResult",
    );
  });

  document.getElementById("timeShiftForm")?.addEventListener("submit", (e) => {
    e.preventDefault();
    runSimulation(
      "timeshift",
      {
        applianceConsumption: parseFloat(
          document.getElementById("applianceConsumption").value,
        ),
        energyTariffEuro: parseFloat(
          document.getElementById("energyTariffTimeShift").value,
        ),
      },
      "timeShiftResult",
    );
  });

  document.getElementById("standbyForm")?.addEventListener("submit", (e) => {
    e.preventDefault();
    runSimulation(
      "standby",
      {
        standbyConsumption: parseFloat(
          document.getElementById("standbyConsumption").value,
        ),
        energyTariffEuro: parseFloat(
          document.getElementById("energyTariffStandby").value,
        ),
      },
      "standbyResult",
    );
  });
});
