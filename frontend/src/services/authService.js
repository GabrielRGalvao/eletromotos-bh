async function dadosDaResposta(resposta) {
  return resposta.json().catch(() => ({}))
}

export async function entrar(dados) {
  const resposta = await fetch("/api/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "same-origin",
    body: JSON.stringify(dados),
  })

  if (!resposta.ok) {
    const corpo = await dadosDaResposta(resposta)
    throw new Error(corpo.mensagem || "Não foi possível entrar.")
  }

  return resposta.json()
}

export async function obterSessao() {
  const resposta = await fetch("/api/auth/me", {
    credentials: "same-origin",
  })

  if (resposta.status === 401) {
    return null
  }

  if (!resposta.ok) {
    throw new Error("Não foi possível verificar o acesso.")
  }

  return resposta.json()
}

export async function sair() {
  await fetch("/api/auth/logout", {
    method: "POST",
    credentials: "same-origin",
  })
}