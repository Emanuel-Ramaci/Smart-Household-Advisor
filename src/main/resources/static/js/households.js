async function refreshHouseholds() {
  const res = await fetch("/api/household/mine");
  const households = await res.json();
  const tbody = document.querySelector("#householdTable tbody");
  tbody.innerHTML = "";

  households.forEach((h, index) => {
    const row = document.createElement("tr");
    row.dataset.id = h.id;

    // Mostriamo address formattato
    const fullAddress = `${h.address}`;
    const formattedEnergyTariff = Number(h.energyTariff)
      .toFixed(2)
      .replace(".", ",");

    row.innerHTML = `
      <td>${index + 1}</td>
      <td class="editable" data-field="name">${h.name}</td>
      <td class="editable" data-field="address">${fullAddress}</td>
      <td class="editable" data-field="squareMeters">${h.squareMeters}</td>
      <td class="editable" data-field="energyClass">${h.energyClass}</td>
      <td class="editable" data-field="energyTariff">${formattedEnergyTariff} €/kWh</td>
      <td class="editable" data-field="residentsCount">${h.residentsCount}</td>
      <td>
        <button class="edit-btn">Modifica</button>
        <button class="delete-btn">Elimina</button>
      </td>
    `;

    tbody.appendChild(row);
  });
}

document
  .querySelector("#householdTable tbody")
  .addEventListener("click", async (e) => {
    const row = e.target.closest("tr");
    if (!row) return;

    const id = row.dataset.id;

    // DELETE
    if (e.target.classList.contains("delete-btn")) {
      if (confirm("Sei sicuro di voler eliminare questa household?")) {
        await fetch(`/api/household/${id}`, { method: "DELETE" });
        refreshHouseholds();
      }
      return;
    }

    // EDIT / CONFIRM
    if (e.target.classList.contains("edit-btn")) {
      const btn = e.target;

      if (btn.innerText === "Modifica") {
        row.querySelectorAll(".editable").forEach((td) => {
          const field = td.dataset.field;
          const val = td.innerText.trim();

          if (field === "address") {
            const parts = val.split(",").map((p) => p.trim());

            const via = parts[0] || "";
            const city = parts[1] || "";
            const postal = parts[2] || "";

            td.innerHTML = `
              <div>
                <input type="text" class="via-input" value="${via}" placeholder="Via" />
                <input type="text" class="city-input" value="${city}" placeholder="Città" />
                <input type="number" class="postal-input" value="${postal}" placeholder="CAP" />
              </div>
            `;
          } else {
            td.innerHTML = `<input type="text" value="${val}" />`;
          }
        });

        btn.innerText = "Conferma";
      } else {
        const payload = {};

        row.querySelectorAll(".editable").forEach((td) => {
          const field = td.dataset.field;

          if (field === "address") {
            const via = td.querySelector(".via-input").value.trim();
            const city = td.querySelector(".city-input").value.trim();
            const postal = td.querySelector(".postal-input").value.trim();

            payload.address = via;
            payload.city = city;
            payload.postalCode = parseInt(postal);
          } else {
            const value = td.querySelector("input").value.trim();

            if (field === "residentsCount") {
              payload[field] = parseInt(value);
            } else if (field === "squareMeters" || field === "energyTariff") {
              payload[field] = parseFloat(value);
            } else {
              payload[field] = value;
            }
          }
        });

        const res = await fetch(`/api/household/${id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        if (res.ok) {
          const data = await res.json();

          const fullAddress = `${data.address}`;

          row.querySelectorAll(".editable").forEach((td) => {
            const field = td.dataset.field;

            if (field === "address") {
              td.innerText = fullAddress;
            } else if (field === "energyTariff") {
              td.innerText =
                Number(data[field]).toFixed(2).replace(".", ",") + " €/kWh";
            } else {
              td.innerText = data[field];
            }
          });

          btn.innerText = "Modifica";
        } else {
          alert("Errore durante l'aggiornamento");
        }
      }
    }
  });

refreshHouseholds();

document
  .getElementById("addHouseholdForm")
  .addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
      name: document.getElementById("name").value,
      address: document.getElementById("address").value,
      city: document.getElementById("city").value,
      postalCode: parseInt(document.getElementById("postalCode").value),
      squareMeters: parseFloat(document.getElementById("squareMeters").value),
      energyClass: document.getElementById("energyClass").value,
      energyTariff: parseFloat(document.getElementById("energyTariff").value),
      residentsCount: parseInt(document.getElementById("residentsCount").value),
    };

    try {
      const res = await fetch("/api/household", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) throw new Error("Errore nella creazione della household");

      document.getElementById("addedHouseholdResult").innerText =
        "Household creata con successo!";

      document.getElementById("addHouseholdForm").reset();
      refreshHouseholds();
    } catch (err) {
      document.getElementById("addedHouseholdResult").innerText =
        `${err.message}`;
    }
  });
