import { apiFetch, atualizarTokenCsrf } from "./api";

async function dadosDaResposta(resposta) {
  return resposta.json().catch(() => ({}));
}

export async function entrar(dados) {
  const resposta = await apiFetch("/api/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(dados),
  });

  if (!resposta.ok) {
    const corpo = await dadosDaResposta(resposta);
    throw new Error(corpo.mensagem || "Não foi possível entrar.");
  }

  const usuario = await resposta.json();

  await atualizarTokenCsrf();

  return usuario;
}

export async function obterSessao() {
  await atualizarTokenCsrf();

  const resposta = await apiFetch("/api/auth/me");

  if (resposta.status === 401) {
    return null;
  }

  if (!resposta.ok) {
    throw new Error("Não foi possível verificar o acesso.");
  }

  return resposta.json();
}

export async function sair() {
  const resposta = await apiFetch("/api/auth/logout", {
    method: "POST",
  });

  if (!resposta.ok) {
    const corpo = await dadosDaResposta(resposta);
    throw new Error(corpo.mensagem || "Não foi possível encerrar a sessão.");
  }
}
