document.addEventListener("DOMContentLoaded", () => {
  const householdElement = document.getElementById("householdId");
  const tbody = document.querySelector("#allEnergyConsumptions tbody");
  const form = document.getElementById("addEnergyConsumptionForm");

  if (!householdElement || !tbody) return;

  const householdId = householdElement.innerText.trim();

  /* DELETE */

  tbody.addEventListener("click", async (e) => {
    if (!e.target.classList.contains("delete-btn")) return;

    const id = e.target.dataset.id;
    if (!id) return;

    if (!confirm("Sei sicuro di voler eliminare questo consumo energetico?"))
      return;

    try {
      await fetch(`/api/energy/${id}`, { method: "DELETE" });
      refreshAllEnergyConsumptions();
    } catch (err) {
      console.error("Errore eliminando consumo energetico:", err);
    }
  });

  /* REFRESH */

  async function refreshAllEnergyConsumptions() {
    try {
      const res = await fetch(`/api/energy/household/${householdId}`);
      const energyConsumptions = await res.json();
      tbody.innerHTML = "";

      energyConsumptions.forEach((ec, index) => {
        const createdAt = ec.createdAt
          ? new Date(ec.createdAt).toLocaleString("it-IT")
          : "";

        const updatedAt = ec.updatedAt
          ? new Date(ec.updatedAt).toLocaleString("it-IT")
          : "";

        const formattedConsumptionKwh = Number(ec.consumptionKwh)
          .toFixed(2)
          .replace(".", ",");

        const refDate = ec.referenceDate
          ? new Date(ec.referenceDate).toLocaleDateString("it-IT")
          : "";

        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${index + 1}</td>
          <td>${ec.category}</td>
          <td>${formattedConsumptionKwh} kWh</td>
          <td>${refDate}</td>
          <td>
            Creato in data: ${createdAt}<br>
            Ultima modifica: ${updatedAt}
          </td>
          <td>
            <button class="delete-btn" data-id="${ec.id}">
              Elimina
            </button>
          </td>
        `;

        tbody.appendChild(row);
      });
    } catch (err) {
      console.error(
        "Errore durante il caricamento dei consumi energetici:",
        err,
      );
    }
  }

  refreshAllEnergyConsumptions();

  /* ADD */

  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const payload = {
        category: document.getElementById("category").value,
        consumptionKwh: parseFloat(
          document.getElementById("consumptionKwh").value,
        ),
        referenceDate: document.getElementById("referenceDate").value,
        householdId: parseInt(householdId),
      };

      try {
        const res = await fetch(`/api/energy/household/${householdId}`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        if (!res.ok)
          throw new Error("Errore nell'inserimento del consumo energetico");

        document.getElementById("addedEnergyConsumptionResult").innerText =
          "Consumo energetico aggiunto con successo!";

        refreshAllEnergyConsumptions();
      } catch (err) {
        console.error(err);
        document.getElementById("addedEnergyConsumptionResult").innerText =
          err.message;
      }
    });
  }
});
