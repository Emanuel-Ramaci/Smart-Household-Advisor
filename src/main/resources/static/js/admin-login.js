document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("loginForm");
  const errorMsg = document.getElementById("errorMsg");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
      const res = await fetch("/api/auth/admin-login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        const msg = await res.text();
        errorMsg.innerText = msg;
        return;
      }

      // Redirect alla dashboard dell'admin
      window.location.href = "/admin/dashboard";
    } catch (err) {
      console.error(err);
      errorMsg.innerText = "Errore durante il login";
    }
  });
});
