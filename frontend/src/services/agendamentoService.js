async function lerResposta(resposta) {
  if (!resposta.ok) {
    const dados = await resposta.json().catch(() => ({}))

    const erro = new Error(
      dados.mensagem || 'Não foi possível completar a operação.'
    )

    erro.campos = dados.campos || {}
    throw erro
  }

  return resposta.json()
}

export async function listarAgendamentos(data, signal) {
  const consulta = data
    ? `?data=${encodeURIComponent(data)}`
    : ''

  const resposta = await fetch(`/api/agendamentos${consulta}`, {
    signal,
  })

  return lerResposta(resposta)
}

export async function cadastrarAgendamento(agendamento) {
  const resposta = await fetch('/api/agendamentos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(agendamento),
  })

  return lerResposta(resposta)
}