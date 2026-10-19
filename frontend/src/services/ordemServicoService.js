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

export async function listarOrdensServico(signal) {
  const resposta = await fetch('/api/ordens-servico', { signal })

  return lerResposta(resposta)
}

export async function receberAgendamento(
  agendamentoId,
  dados,
  video
) {
  const formulario = new FormData()

  formulario.append(
    'dados',
    new Blob([JSON.stringify(dados)], {
      type: 'application/json',
    })
  )

  formulario.append('video', video)

  const resposta = await fetch(
    `/api/ordens-servico/receber-agendamento/${agendamentoId}`,
    {
      method: 'POST',
      body: formulario,
    }
  )

  return lerResposta(resposta)
}