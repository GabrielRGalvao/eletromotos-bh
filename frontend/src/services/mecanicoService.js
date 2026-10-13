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

export async function listarMecanicos(signal) {
  const resposta = await fetch('/api/mecanicos', { signal })
  return lerResposta(resposta)
}

export async function salvarMecanico(id, nome) {
  const editando = id !== null

  const resposta = await fetch(
    editando ? `/api/mecanicos/${id}` : '/api/mecanicos',
    {
      method: editando ? 'PUT' : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome }),
    }
  )

  return lerResposta(resposta)
}

export async function alterarStatusMecanico(id, ativo) {
  const resposta = await fetch(`/api/mecanicos/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ ativo }),
  })

  return lerResposta(resposta)
}