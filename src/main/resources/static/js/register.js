document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("registerForm");
  if (!form) return; // Questo evita errori se la pagina non ha quel form

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
      email: document.getElementById("email").value,
      firstName: document.getElementById("firstName").value,
      lastName: document.getElementById("lastName").value,
      password: document.getElementById("password").value,
    };

    try {
      const res = await fetch("/api/me/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const err = await res.text();
        document.getElementById("msg").innerText = err;
        return;
      }

      document.getElementById("msg").style.color = "green";
      document.getElementById("msg").innerText =
        "Registrazione completata. Effettua il login.";

      setTimeout(() => {
        window.location.href = "/user-login";
      }, 2500);
    } catch (err) {
      console.error(err);
      document.getElementById("msg").innerText =
        "Errore durante la registrazione";
    }
  });
});
