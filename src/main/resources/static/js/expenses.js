document.addEventListener("DOMContentLoaded", () => {
  const householdElement = document.getElementById("householdId");
  const tbody = document.querySelector("#allExpenses tbody");
  const form = document.getElementById("addExpenseForm");

  if (!householdElement || !tbody) return;

  const householdId = householdElement.innerText.trim();

  /* DELETE */

  tbody.addEventListener("click", async (e) => {
    if (!e.target.classList.contains("delete-btn")) return;

    const id = e.target.dataset.id;
    if (!id) return;

    if (!confirm("Sei sicuro di voler eliminare questa spesa?")) return;

    try {
      await fetch(`/api/expense/${id}`, { method: "DELETE" });
      refreshAllExpenses();
    } catch (err) {
      console.error("Errore eliminando spesa:", err);
    }
  });

  /* REFRESH */

  async function refreshAllExpenses() {
    try {
      const res = await fetch(`/api/expense/household/${householdId}`);
      const expenses = await res.json();
      tbody.innerHTML = "";

      expenses.forEach((ex, index) => {
        const createdAt = ex.createdAt
          ? new Date(ex.createdAt).toLocaleString("it-IT")
          : "";

        const updatedAt = ex.updatedAt
          ? new Date(ex.updatedAt).toLocaleString("it-IT")
          : "";

        const formattedAmount = Number(ex.amount).toFixed(2).replace(".", ",");

        const refDate = ex.referenceDate
          ? new Date(ex.referenceDate).toLocaleDateString("it-IT")
          : "";

        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${index + 1}</td>
          <td>${ex.category}</td>
          <td>${formattedAmount} €</td>
          <td>${refDate}</td>
          <td>${ex.description}</td>
          <td>
            Creata in data: ${createdAt}<br>
            Ultima modifica: ${updatedAt}
          </td>
          <td>
            <button class="delete-btn" data-id="${ex.id}">
              Elimina
            </button>
          </td>
        `;

        tbody.appendChild(row);
      });
    } catch (err) {
      console.error("Errore durante il caricamento delle spese:", err);
    }
  }

  refreshAllExpenses();

  /* ADD */

  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const payload = {
        category: document.getElementById("category").value,
        amount: parseFloat(document.getElementById("amount").value),
        referenceDate: document.getElementById("referenceDate").value,
        description: document.getElementById("description").value,
        householdId: parseInt(householdId),
      };

      try {
        const res = await fetch(`/api/expense/household/${householdId}`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        if (!res.ok) throw new Error("Errore nell'inserimento della spesa");

        document.getElementById("addedExpenseResult").innerText =
          "Spesa aggiunta con successo!";

        refreshAllExpenses();
      } catch (err) {
        console.error(err);
        document.getElementById("addedExpenseResult").innerText = err.message;
      }
    });
  }
});
