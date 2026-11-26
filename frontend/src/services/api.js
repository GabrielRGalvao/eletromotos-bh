function lerCookie(nome) {
  const prefixo = `${nome}=`;
  const cookies = document.cookie.split(";");

  for (const cookie of cookies) {
    const valor = cookie.trim();

    if (valor.startsWith(prefixo)) {
      return decodeURIComponent(valor.slice(prefixo.length));
    }
  }

  return null;
}

export async function atualizarTokenCsrf() {
  const resposta = await fetch("/api/auth/csrf", {
    method: "GET",
    credentials: "same-origin",
  });

  if (!resposta.ok) {
    throw new Error("Não foi possível preparar a proteção de segurança.");
  }

  await resposta.json();

  const token = lerCookie("XSRF-TOKEN");

  if (!token) {
    throw new Error("Não foi possível obter o token de segurança.");
  }

  return token;
}

export async function apiFetch(url, opcoes = {}) {
  const metodo = (opcoes.method || "GET").toUpperCase();
  const metodosProtegidos = ["POST", "PUT", "PATCH", "DELETE"];
  const headers = new Headers(opcoes.headers || {});

  if (metodosProtegidos.includes(metodo)) {
    let token = lerCookie("XSRF-TOKEN");

    if (!token) {
      token = await atualizarTokenCsrf();
    }

    headers.set("X-XSRF-TOKEN", token);
  }

  return fetch(url, {
    ...opcoes,
    method: metodo,
    headers,
    credentials: "same-origin",
  });
}
