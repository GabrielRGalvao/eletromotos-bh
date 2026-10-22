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

export async function registrarDiagnostico(
  ordemServicoId,
  dados
) {
  const resposta = await fetch(
    `/api/ordens-servico/${ordemServicoId}/diagnostico`,
    {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(dados),
    }
  )

  return lerResposta(resposta)
}

export async function registrarDecisaoOrcamento(
  ordemServicoId,
  dados
) {
  const resposta = await fetch(
    `/api/ordens-servico/${ordemServicoId}/decisao-orcamento`,
    {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(dados),
    }
  )

  return lerResposta(resposta)
}