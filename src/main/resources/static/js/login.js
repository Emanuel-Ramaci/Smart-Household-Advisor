document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");
  const errorMsg = document.getElementById("errorMsg");

  if (!form) return; // Questo evita errori se lo script viene caricato altrove

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
      const res = await fetch("/api/auth/user-login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        const msg = await res.text();
        errorMsg.innerText = msg;
        return;
      }

      window.location.href = "/home";
    } catch (err) {
      console.error(err);
      errorMsg.innerText = "Errore durante il login";
    }
  });
});
