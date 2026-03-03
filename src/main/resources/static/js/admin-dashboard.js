function toggleSection(id) {
  const table = document.getElementById(id);
  table.classList.toggle("collapsed");
}

async function toggleActive(btn) {
  const id = btn.getAttribute("data-id");
  const res = await fetch(`/api/admin/${id}`, { method: "PUT" });

  if (res.ok) {
    const row = btn.closest("tr");
    const activeCell = row.children[4]; // colonna "Attivo"
    const currentValue = activeCell.innerText.trim();
    activeCell.innerText = currentValue === "true" ? "false" : "true";
  }
}

async function deleteUser(btn) {
  const id = btn.getAttribute("data-id");
  const res = await fetch(`/api/admin/${id}`, { method: "DELETE" });

  if (res.ok) {
    btn.closest("tr").remove();
  }
}

document
  .getElementById("createUserForm")
  .addEventListener("submit", async (e) => {
    e.preventDefault();
    const roleInput = document.getElementById("roleNames").value; // es. "USER, ADMIN"
    const roleNames = roleInput.split(",").map((r) => r.trim()); // ["USER", "ADMIN"]
    const payload = {
      email: document.getElementById("email").value,
      firstName: document.getElementById("firstName").value,
      lastName: document.getElementById("lastName").value,
      password: document.getElementById("password").value,
      roleNames: roleNames,
    };
    const res = await fetch("/api/admin/users", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (res.ok) {
      const newUser = await res.json();
      document.getElementById("userMsg").style.color = "green";
      document.getElementById("userMsg").innerText =
        "Utente creato con successo!";
      addUserRow(newUser);
    } else {
      document.getElementById("userMsg").style.color = "red";
      document.getElementById("userMsg").innerText =
        "Errore durante la creazione dell'utente";
    }
  });

function addUserRow(user) {
  const table = document.getElementById("usersTable");
  const tbody = table.querySelector("tbody");

  const row = document.createElement("tr");

  row.innerHTML = `
    <td>${user.id}</td>
    <td>${user.email}</td>
    <td>${user.firstName}</td>
    <td>${user.lastName}</td>
    <td>${user.active}</td>
    <td>${user.roleNames}</td>
    <td>
      <button type="button" data-id="${user.id}" onclick="toggleActive(this)">
        Attiva/Disattiva
      </button>
      <button type="button" data-id="${user.id}" onclick="deleteUser(this)">
        Elimina
      </button>
    </td>
  `;

  tbody.appendChild(row);
}

document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".result-data").forEach((el) => {
    try {
      const parsed = JSON.parse(el.innerText);
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

      el.innerHTML = Object.entries(parsed)
        .map(([k, v]) => {
          if (
            k === "estimatedTotalPriceEuro" ||
            k === "estimatedReducedConsumptionKwh" ||
            k === "estimatedEliminatedConsumptionKwh"
          ) {
            // numero a 2 decimali + sostituisco . con ,
            v = Number(v).toFixed(2).replace(".", ",");
          }
          return `${labels[k] || k}: ${v}${units[k] ? " " + units[k] : ""}`;
        })
        .join("<br>");
    } catch (e) {
      // se non è JSON valido, rimane il testo originale
    }
  });
});
